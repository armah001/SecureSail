package com.example.amalisecuresail.service;


import com.example.amalisecuresail.dto.LoginDTO;
import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.payload.ChangePasswordRequest;
import com.example.amalisecuresail.payload.ForgotPasswordRequest;
import com.example.amalisecuresail.payload.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    String generateResetToken(ForgotPasswordRequest request);
    User validateResetToken(String token);
    void invalidateResetToken(String token);
    void updateUserPassword(User user, String newPassword);

    void updateProfile(UpdateProfileRequest request, Principal connectedUser);


    List<User> findConnectedUsers();

    LoginDTO getCurrentUserDetails();
}