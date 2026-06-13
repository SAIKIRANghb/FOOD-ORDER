package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.LoginRequest;
import com.hungrezy.foodordering.dto.request.RefreshTokenRequest;
import com.hungrezy.foodordering.dto.request.RegisterRequest;
import com.hungrezy.foodordering.dto.response.AuthResponse;
import com.hungrezy.foodordering.dto.response.VerificationResponse;
import com.hungrezy.foodordering.entity.RefreshToken;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.entity.VerificationCode;
import com.hungrezy.foodordering.exception.BadRequestException;
import com.hungrezy.foodordering.exception.ServiceUnavailableException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.RefreshTokenRepository;
import com.hungrezy.foodordering.repository.UserRepository;
import com.hungrezy.foodordering.repository.VerificationCodeRepository;
import com.hungrezy.foodordering.security.JwtProperties;
import com.hungrezy.foodordering.security.JwtUtil;
import com.hungrezy.foodordering.service.AuthService;
import com.hungrezy.foodordering.service.OtpDeliveryService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/auth", "/auth"})
public class AuthController {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(10);

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final DtoMapper mapper;
    private final OtpDeliveryService otpDeliveryService;

    public AuthController(AuthService authService, UserRepository userRepository, JwtUtil jwtUtil,
                          JwtProperties jwtProperties, RefreshTokenRepository refreshTokenRepository,
                          VerificationCodeRepository verificationCodeRepository, DtoMapper mapper,
                          OtpDeliveryService otpDeliveryService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.mapper = mapper;
        this.otpDeliveryService = otpDeliveryService;
    }

    @PostMapping("/register")
    ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Registered successfully", authService.register(request));
    }

    @PostMapping("/login")
    ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Logged in successfully", authService.login(request));
    }

    @PostMapping("/refresh")
    ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok("Token refreshed", authService.refresh(request));
    }

    @PostMapping({"/{role}/signin", "/signin"})
    ApiResponse<AuthResponse> signin(@PathVariable(required = false) String role, @Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Logged in successfully", authService.login(request));
    }

    @PostMapping("/{role}/signup")
    ApiResponse<AuthResponse> signup(@PathVariable String role, @Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Registered successfully", authService.register(role, request));
    }

    @GetMapping("/checkUser/{mobileNumber}")
    Map<String, Object> checkUser(@PathVariable String mobileNumber) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", userRepository.findByMobileNumber(mobileNumber).map(mapper::toUserResponse).orElse(null));
        return response;
    }

    @PostMapping({"/send-verification-code", "/sendOTP", "/signin-otp/send-verification-code",
            "//signin-otp/send-verification-code"})
    @Transactional
    ApiResponse<VerificationResponse> sendVerificationCode(@RequestBody Map<String, String> request) {
        String identifier = normalizedIdentifier(request);
        if (!identifier.contains("@")) {
            throw new ServiceUnavailableException("SMS delivery is not configured");
        }
        String flow = request.getOrDefault("flow", "signup");
        String code = "%06d".formatted(RANDOM.nextInt(1_000_000));
        otpDeliveryService.sendEmailVerificationCode(identifier, code);
        verificationCodeRepository.deleteByFlowAndIdentifier(flow, identifier);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setFlow(flow);
        verificationCode.setIdentifier(identifier);
        verificationCode.setCode(code);
        verificationCode.setExpiresAt(Instant.now().plus(VERIFICATION_CODE_TTL));
        verificationCodeRepository.save(verificationCode);

        return ApiResponse.ok("Verification code sent", new VerificationResponse(false));
    }

    @PostMapping({"/verify-code", "/verifyOTP"})
    @Transactional
    ApiResponse<VerificationResponse> verifyCode(@RequestBody Map<String, String> request) {
        verifyRequestCode(request.getOrDefault("flow", "signup"), request);
        return ApiResponse.ok("Verification successful", new VerificationResponse(true));
    }

    @PostMapping({"/signin-otp/verify-code", "//signin-otp/verify-code"})
    @Transactional
    ApiResponse<AuthResponse> verifySigninCode(@RequestBody Map<String, String> request) {
        String email = verifyRequestCode("signup", request);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("User not found"));
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);
        saveRefreshToken(user, refreshToken);

        return ApiResponse.ok("Verification successful", new AuthResponse(accessToken, refreshToken, mapper.toUserResponse(user)));
    }

    private String verifyRequestCode(String flow, Map<String, String> request) {
        String identifier = normalizedIdentifier(request);
        String submittedCode = firstNonBlank(request.get("verificationCode"), request.get("OTP"), request.get("otp"));
        if (submittedCode == null || submittedCode.isBlank()) {
            throw new BadRequestException("Verification code is required");
        }

        VerificationCode expectedCode = verificationCodeRepository.findFirstByFlowAndIdentifierOrderByCreatedAtDesc(flow, identifier)
                .orElse(null);
        if (expectedCode == null || expectedCode.getExpiresAt().isBefore(Instant.now())) {
            verificationCodeRepository.deleteByFlowAndIdentifier(flow, identifier);
            throw new BadRequestException("Verification code expired. Please request a new code.");
        }
        if (!expectedCode.getCode().equals(submittedCode.trim())) {
            throw new BadRequestException("Invalid verification code");
        }

        verificationCodeRepository.deleteByFlowAndIdentifier(flow, identifier);
        return identifier;
    }

    private String normalizedIdentifier(Map<String, String> request) {
        String email = request.get("email");
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase();
        }
        String mobileNumber = request.get("mobileNumber");
        if (mobileNumber != null && !mobileNumber.isBlank()) {
            return mobileNumber.trim();
        }
        throw new BadRequestException("Email or mobile number is required");
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private void saveRefreshToken(User user, String token) {
        refreshTokenRepository.deleteByEmail(user.getEmail());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setEmail(user.getEmail());
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(jwtProperties.getRefreshTokenDays())));
        refreshTokenRepository.save(refreshToken);
    }
}
