package com.example.amalisecuresail.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing user information.
 */
@Getter
@AllArgsConstructor
@Builder
public class UserDto {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private String uuid;
    private boolean verified;
    private boolean accountLocked;
}

