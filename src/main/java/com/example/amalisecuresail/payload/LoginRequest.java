package com.example.amalisecuresail.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Models the login credentials sent by user
 */
@Data
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String email;
    private String password;
    private String oauthToken;
}