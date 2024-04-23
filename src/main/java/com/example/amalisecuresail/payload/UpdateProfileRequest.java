package com.example.amalisecuresail.payload;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;

}