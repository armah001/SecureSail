package com.example.amalisecuresail.controller;


import com.example.amalisecuresail.dto.LoginDTO;
import com.example.amalisecuresail.dto.Response;
import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.payload.ChangePasswordRequest;
import com.example.amalisecuresail.payload.ForgotPasswordRequest;
import com.example.amalisecuresail.payload.ResetPasswordRequest;
import com.example.amalisecuresail.service.EmailService;
import com.example.amalisecuresail.service.UserAccountService;
import com.example.amalisecuresail.service.UserService;
import com.example.amalisecuresail.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;

/**
 * UserController handles HTTP requests related to user operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserAccountService userAccountService;
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationService verificationService;


    @PatchMapping("/change-password")
    public ResponseEntity<Response> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        userService.changePassword(request, connectedUser);
        Response responseBody = new Response(new Response.Status(HttpStatus.OK.value(), "Password changed successfully"));
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/forgot-password")

    public ResponseEntity<Response> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = userService.generateResetToken(request);
        String frontendUrl = System.getenv("FRONTEND_URL");
        Response responseBody = new Response(new Response.Status(HttpStatus.OK.value(), "Password reset email sent successfully"));
        String resetPasswordLink = frontendUrl + "/verifytoken?token=" + token;
        emailService.sendMail(null, request.getEmail(), new String[0], "Password reset", "Click on the link below to reset your password: " ,resetPasswordLink );
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/password-reset/{token}")
    public ResponseEntity<Response> validateTokenAndGetUser(@PathVariable String token) {
        User user = userService.validateResetToken(token);
        String message = "This is a token for " + user.getFullName() + " to reset their password.";
        Response responseBody = new Response(new Response.Status(HttpStatus.OK.value(), message));
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/password-reset/{token}/reset")
    public ResponseEntity<Response> resetPassword(@PathVariable String token, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        User user = userService.validateResetToken(token);
        userService.updateUserPassword(user, resetPasswordRequest.getNewPassword());
        userService.invalidateResetToken(token);
        String message = "Password reset successfully for " + user.getFullName();
        Response responseBody = new Response(new Response.Status(HttpStatus.OK.value(), message));

        return ResponseEntity.ok(responseBody);
    }


    @GetMapping("/verify")
    public RedirectView verifyUser(@RequestParam("token") String token) {
        verificationService.verifyUser(token);
//        String frontendDeployedLink = System.getenv("FRONTEND_URL");
        String loginUrl = "localhost:8080";
        return new RedirectView(loginUrl);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    @GetMapping("/current-user-details")
    public ResponseEntity<Response> getCurrentUserDetails() {
        LoginDTO userDetails = userService.getCurrentUserDetails();
        Response response = new Response(new Response.Status(HttpStatus.OK.value(), "User details queried successfully"), userDetails);
        return ResponseEntity.ok(response);
    }

}

