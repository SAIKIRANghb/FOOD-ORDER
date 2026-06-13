package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.dto.request.LoginRequest;
import com.hungrezy.foodordering.dto.request.RefreshTokenRequest;
import com.hungrezy.foodordering.dto.request.RegisterRequest;
import com.hungrezy.foodordering.dto.response.AuthResponse;
import com.hungrezy.foodordering.entity.RefreshToken;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.entity.enums.Role;
import com.hungrezy.foodordering.exception.BadRequestException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.RefreshTokenRepository;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import com.hungrezy.foodordering.repository.UserRepository;
import com.hungrezy.foodordering.security.JwtProperties;
import com.hungrezy.foodordering.security.JwtUtil;
import com.hungrezy.foodordering.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final DtoMapper mapper;

    public AuthServiceImpl(UserRepository userRepository, RestaurantRepository restaurantRepository,
                           RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                           JwtProperties jwtProperties, DtoMapper mapper) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        return registerWithRole(request, request.getRole() == null ? Role.USER : request.getRole());
    }

    @Override
    @Transactional
    public AuthResponse register(String role, RegisterRequest request) {
        return registerWithRole(request, roleFromPath(role));
    }

    private AuthResponse registerWithRole(RegisterRequest request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (request.getMobileNumber() != null && userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number already registered");
        }
        User user = new User();
        user.setEmail(request.getEmail().toLowerCase());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(firstNonBlank(request.getFirstName(), request.getName(), request.getEmail().split("@")[0]));
        user.setLastName(request.getLastName());
        user.setRole(role);
        User saved = userRepository.save(user);
        ensureRestaurantProfile(saved, firstNonBlank(request.getName(), request.getFirstName(), saved.getEmail()));
        return tokensFor(saved);
    }

    private Role roleFromPath(String role) {
        return switch (role == null ? "" : role.toLowerCase()) {
            case "admin" -> Role.ADMIN;
            case "restaurant" -> Role.RESTAURANT_OWNER;
            default -> Role.USER;
        };
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = request.getEmail() != null && !request.getEmail().isBlank()
                ? userRepository.findByEmail(request.getEmail()).orElseThrow()
                : userRepository.findByMobileNumber(request.getMobileNumber()).orElseThrow();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword()));
        ensureRestaurantProfile(user, firstNonBlank(user.getFirstName(), user.getEmail()));
        return tokensFor(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String email = jwtUtil.subject(request.getRefreshToken());
        RefreshToken saved = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        if (!email.equals(saved.getEmail()) || saved.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(saved);
            throw new BadRequestException("Invalid refresh token");
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        return tokensFor(user);
    }

    private AuthResponse tokensFor(User user) {
        String access = jwtUtil.createAccessToken(user);
        String refresh = jwtUtil.createRefreshToken(user);
        refreshTokenRepository.deleteByEmail(user.getEmail());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setEmail(user.getEmail());
        refreshToken.setToken(refresh);
        refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(jwtProperties.getRefreshTokenDays())));
        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(access, refresh, mapper.toUserResponse(user));
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private void ensureRestaurantProfile(User user, String fallbackName) {
        if (user.getRole() != Role.RESTAURANT_OWNER || restaurantRepository.findFirstByOwnerId(user.getId()).isPresent()) {
            return;
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setOwner(user);
        restaurant.setName(firstNonBlank(fallbackName, user.getEmail()));
        restaurant.setDescription("Restaurant profile");
        restaurant.setCuisineType("Multi Cuisine");
        restaurantRepository.save(restaurant);
    }
}
