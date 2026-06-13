package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.dto.request.AddressRequest;
import com.hungrezy.foodordering.dto.response.UserResponse;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.UserRepository;
import com.hungrezy.foodordering.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public UserServiceImpl(UserRepository userRepository, DtoMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserResponse profile(String email) {
        return mapper.toUserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Override
    @Transactional
    public UserResponse addAddress(String email, AddressRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getAddresses().add(mapper.toAddress(request));
        return mapper.toUserResponse(user);
    }
}
