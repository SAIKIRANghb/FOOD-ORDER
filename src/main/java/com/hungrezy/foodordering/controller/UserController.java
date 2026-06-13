package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.AddressRequest;
import com.hungrezy.foodordering.dto.response.UserResponse;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.entity.enums.UserStatus;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.UserRepository;
import com.hungrezy.foodordering.security.JwtUtil;
import com.hungrezy.foodordering.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final DtoMapper mapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, DtoMapper mapper, JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/api/users/profile")
    ApiResponse<UserResponse> profile(Principal principal) {
        return ApiResponse.ok("Profile fetched", userService.profile(principal.getName()));
    }

    @PostMapping("/api/users/address")
    ApiResponse<UserResponse> addAddress(Principal principal, @RequestBody AddressRequest request) {
        return ApiResponse.ok("Address added", userService.addAddress(principal.getName(), request));
    }

    @GetMapping("/api/user/count")
    ApiResponse<Long> count() {
        return ApiResponse.ok("Users counted", userRepository.count());
    }

    @GetMapping("/api/user/all")
    ApiResponse<List<UserResponse>> all() {
        return ApiResponse.ok("Users fetched", userRepository.findAll().stream().map(mapper::toUserResponse).toList());
    }

    @GetMapping("/api/user/{id}")
    ApiResponse<UserResponse> get(@PathVariable UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ApiResponse.ok("User fetched", mapper.toUserResponse(user));
    }

    @PutMapping("/api/user/{id}/updateStatus")
    @Transactional
    ApiResponse<UserResponse> updateStatus(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String status = body == null ? null : body.get("status");
        if (status != null) {
            user.setStatus("active".equalsIgnoreCase(status) ? UserStatus.ACTIVE : UserStatus.SUSPENDED);
        }
        return ApiResponse.ok("User status updated", mapper.toUserResponse(user));
    }

    @PostMapping("/api/user/{id}/upload/image")
    @Transactional
    ApiResponse<Map<String, Object>> uploadImage(@PathVariable UUID id, @RequestParam("image") MultipartFile image) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setImageUrl(image == null || image.isEmpty() ? user.getImageUrl() : image.getOriginalFilename());
        String accessToken = jwtUtil.createAccessToken(user);
        return ApiResponse.ok("Image uploaded", Map.of("token", Map.of(
                "user", mapper.toUserResponse(user),
                "accessToken", accessToken
        )));
    }

    @PutMapping({"/user/updateDetails", "/api/user/updateDetails"})
    @Transactional
    ApiResponse<UserResponse> updateDetails(@RequestBody UpdateDetailsRequest request) {
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Map<String, String> updates = request.getUpdates();
        if (updates != null) {
            if (updates.get("firstName") != null) user.setFirstName(updates.get("firstName"));
            if (updates.get("lastName") != null) user.setLastName(updates.get("lastName"));
            if (updates.get("email") != null) user.setEmail(updates.get("email").toLowerCase());
            if (updates.get("password") != null && !updates.get("password").isBlank()) {
                user.setPassword(passwordEncoder.encode(updates.get("password")));
            }
        }
        return ApiResponse.ok("User details updated", mapper.toUserResponse(user));
    }

    @Getter
    @Setter
    private static class UpdateDetailsRequest {
        private String mobileNumber;
        private Map<String, String> updates;
    }
}
