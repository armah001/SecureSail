package com.example.amalisecuresail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * models user data response to client
 * sends:
 *          uuid of user
 *          fullName of user
 *          email of user
 *          phoneNumber of user
 */
@Data
@Builder
@AllArgsConstructor
public class LoginDTO {
    private String uuid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profileUrl;
    private String role;
}
