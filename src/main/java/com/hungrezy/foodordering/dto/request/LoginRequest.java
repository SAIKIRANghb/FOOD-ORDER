package com.hungrezy.foodordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String mobileNumber;

    @NotBlank
    private String password;

    public String getIdentifier() {
        return email != null && !email.isBlank() ? email : mobileNumber;
    }
}
