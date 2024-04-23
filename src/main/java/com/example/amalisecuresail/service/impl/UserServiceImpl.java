package com.example.amalisecuresail.service.impl;


import com.example.amalisecuresail.dto.LoginDTO;
import com.example.amalisecuresail.entity.PasswordResetToken;
import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.mapper.UserMapper;
import com.example.amalisecuresail.payload.*;
import com.example.amalisecuresail.entity.enums.ActivityStatus;
import com.example.amalisecuresail.repository.TokenRepository;
import com.example.amalisecuresail.repository.UserRepository;
import com.example.amalisecuresail.service.UserService;
import com.example.amalisecuresail.util.AuthenticationHelpers;
import com.example.amalisecuresail.util.GeneralSystemValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * UserService handles business logic related to user operations, including password management and token generation.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final GeneralSystemValidation validator;


    @Override
    public List<User> findConnectedUsers() {
        return userRepository.findAllByActivityStatus(ActivityStatus.ONLINE);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        User user = getUserFromPrincipal(connectedUser);
        validateAndChangePassword(request, user);
        userRepository.save(user);
    }

    @Override
    public String generateResetToken(ForgotPasswordRequest request) {
        User user = getUserByEmail(request.getEmail());
        PasswordResetToken token = generateAndSaveToken(user);
        return token.getToken();
    }

    @Override
    public User validateResetToken(String token) {
        PasswordResetToken resetToken = getResetToken(token);

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Token has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token has expired");
        }

        return resetToken.getUser();
    }

    @Override
    public void invalidateResetToken(String token) {
        PasswordResetToken resetToken = getResetToken(token);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    @Override
    public void updateUserPassword(User user, String newPassword) {
        validator.validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updateProfile(UpdateProfileRequest request, Principal connectedUser) {
        User user = getUserFromPrincipal(connectedUser);
        validateAndSaveProfile(request, user);
    }


    private void validateAndChangePassword(ChangePasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        validator.validatePassword(request.getNewPassword());

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private void validateAndSaveProfile(UpdateProfileRequest request, User user) {
        validator.validatePhoneNumber(request.getPhoneNumber());
        userMapper.INSTANCE.updateProfileFromDto(request, user);
        userRepository.save(user);
    }

    private PasswordResetToken generateAndSaveToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        return tokenRepository.save(token);
    }

    private User getUserFromPrincipal(Principal connectedUser) {
        return (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
    }

    private PasswordResetToken getResetToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public LoginDTO getCurrentUserDetails() {
        User currentUser = AuthenticationHelpers.getAuthenticatedUserWithErrorHandling();
        User userDetails = userRepository.findByUuid(currentUser.getUuid())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return LoginDTO.builder()
                .uuid(userDetails.getUuid())
                .fullName(userDetails.getFullName())
                .phoneNumber(userDetails.getPhoneNumber())
                .email(userDetails.getEmail())
                .profileUrl(userDetails.getProfilePictureUrl())
                .role(userDetails.getRole().name())
                .build();
    }
}
