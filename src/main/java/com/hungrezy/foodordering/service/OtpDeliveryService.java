package com.hungrezy.foodordering.service;

public interface OtpDeliveryService {
    void sendEmailVerificationCode(String email, String code);
}
