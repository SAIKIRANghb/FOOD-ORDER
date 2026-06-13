package com.hungrezy.foodordering.security;

import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.entity.Restaurant;
import com.hungrezy.foodordering.entity.enums.Role;
import com.hungrezy.foodordering.repository.RestaurantRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final JwtProperties properties;
    private final RestaurantRepository restaurantRepository;
    private final SecretKey key;

    public JwtUtil(JwtProperties properties, RestaurantRepository restaurantRepository) {
        this.properties = properties;
        this.restaurantRepository = restaurantRepository;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(User user) {
        return createToken(user, properties.getAccessTokenMinutes() * 60);
    }

    public String createRefreshToken(User user) {
        return createToken(user, properties.getRefreshTokenDays() * 24 * 60 * 60);
    }

    public String subject(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    private String createToken(User user, long ttlSeconds) {
        Instant now = Instant.now();
        Restaurant restaurant = user.getRole() == Role.RESTAURANT_OWNER
                ? restaurantRepository.findFirstByOwnerId(user.getId()).orElse(null)
                : null;
        String frontendRole = frontendRole(user.getRole());
        String frontendId = restaurant == null ? user.getId().toString() : restaurant.getId().toString();

        Map<String, Object> tokenUser = new HashMap<>();
        tokenUser.put("_id", frontendId);
        tokenUser.put("id", user.getId().toString());
        tokenUser.put("email", user.getEmail());
        tokenUser.put("firstName", restaurant == null ? user.getFirstName() : restaurant.getName());
        tokenUser.put("lastName", user.getLastName());
        tokenUser.put("name", restaurant == null ? user.getFirstName() : restaurant.getName());
        tokenUser.put("mobileNumber", user.getMobileNumber());
        tokenUser.put("role", user.getRole().name());
        tokenUser.put("user_role", frontendRole);
        if (restaurant != null) {
            tokenUser.put("restaurantId", restaurant.getId().toString());
            tokenUser.put("imageUrl", restaurant.getImageUrl());
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId().toString());
        claims.put("user_role", frontendRole);
        claims.put("user", tokenUser);

        return Jwts.builder()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key)
                .compact();
    }

    private String frontendRole(Role role) {
        return switch (role) {
            case ADMIN -> "admin";
            case RESTAURANT_OWNER -> "restaurant";
            default -> "user";
        };
    }
}
