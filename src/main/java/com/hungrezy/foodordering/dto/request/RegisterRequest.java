package com.hungrezy.foodordering.dto.request;

import com.hungrezy.foodordering.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    private String mobileNumber;

    @NotBlank
    @Size(min = 8)
    private String password;

    private String firstName;
    private String lastName;
    private String name;
    private Role role;
}
