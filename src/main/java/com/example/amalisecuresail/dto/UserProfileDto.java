package com.example.amalisecuresail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private String fullName;
    private String phoneNumber;

}