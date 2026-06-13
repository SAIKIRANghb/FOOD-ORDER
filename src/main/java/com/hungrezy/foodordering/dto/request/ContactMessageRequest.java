package com.hungrezy.foodordering.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String subject;

    @NotBlank
    private String message;
}
