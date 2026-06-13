package com.hungrezy.foodordering.service;

import com.hungrezy.foodordering.dto.request.AddressRequest;
import com.hungrezy.foodordering.dto.response.UserResponse;

public interface UserService {
    UserResponse profile(String email);
    UserResponse addAddress(String email, AddressRequest request);
}
