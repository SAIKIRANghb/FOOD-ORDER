package com.hungrezy.foodordering.dto.response;

import com.hungrezy.foodordering.entity.enums.Role;
import com.hungrezy.foodordering.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private Role role;
    private UserStatus status;
    private String imageUrl;
    private Instant createdAt;

    public UserResponse(UUID id, String email, String firstName, String lastName, Role role) {
        this(id, email, firstName, lastName, null, role, UserStatus.ACTIVE, null, null);
    }

    public UserResponse(UUID id, String email, String firstName, String lastName, String mobileNumber, Role role) {
        this(id, email, firstName, lastName, mobileNumber, role, UserStatus.ACTIVE, null, null);
    }

    @com.fasterxml.jackson.annotation.JsonProperty("_id")
    public UUID getIdAlias() {
        return id;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("active")
    public Boolean getActiveAlias() {
        return status == UserStatus.ACTIVE;
    }
}
