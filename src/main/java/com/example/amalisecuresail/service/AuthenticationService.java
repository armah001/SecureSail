package com.example.amalisecuresail.service;

import com.example.amalisecuresail.dto.*;
import com.example.amalisecuresail.entity.*;
import com.example.amalisecuresail.entity.enums.Role;
import com.example.amalisecuresail.payload.*;

import com.example.amalisecuresail.config.JwtService;
import com.example.amalisecuresail.repository.LoginsRepository;
import com.example.amalisecuresail.repository.UserRepository;
import com.example.amalisecuresail.util.EncryptionUtil;
import com.example.amalisecuresail.util.GeneralSystemValidation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for handling user authentication-related operations.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final GeneralSystemValidation authValidation;
    private final LoginsRepository loginsRepository;
    private final EmailService emailService;


//    private final String frontEndUrl = System.getenv("FRONTEND_URL");

    public Response register(RegisterRequest request) {

            return registerManually(request);

    }

    @NotNull
    private Response getResponse(User user) {
        var jwtToken = jwtService.generateToken(user);

        var userResponse = LoginDTO.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("user", userResponse);
        data.put("token", jwtToken);

        return new Response(new Response.Status(HttpStatus.CREATED.value(), "User was created successfully"), data);
    }

    private Response registerManually(RegisterRequest request) {
        try {
            authValidation.validatePassword(request.getPassword());
            authValidation.validatePhoneNumber(request.getPhoneNumber());
            authValidation.validateEmail(request.getEmail());

            var user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .uuid(UUID.randomUUID().toString())
                    .phoneNumber(request.getPhoneNumber())
                    .createdAt(LocalDateTime.now())
                    .verified(false)
                    .build();



            repository.save(user);

            var userAccount = UserAccount.builder()
                    .user(user)
                    .build();

            String encryptedUuid = EncryptionUtil.encrypt(user.getUuid());
            String encodedUuid = URLEncoder.encode(encryptedUuid, StandardCharsets.UTF_8);
            sendEmailVerification(user.getEmail(), encodedUuid);

            user.setUserAccount(userAccount);
            repository.save(user);





            return getResponse(user);

        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolationException(e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmailVerification(String email, String encodedUuid) {
        String backendDeployedLink = System.getenv("BACKEND_LOCAL_URL");
        String verificationUrl = backendDeployedLink + "/api/v1/users/verify?token=" + encodedUuid;
        String[] noCc = new String[0];
        emailService.sendMail(null, email, noCc, "Account Verification", "Click on the button below to verify your account", verificationUrl);
    }



    public Response authenticate(LoginRequest request) {

        if (request.getPassword() != null) {
            return authenticateWithEmailAndPassword(request);
        } else {
            return new Response(new Response.Status(HttpStatus.UNAUTHORIZED.value(), "No authentication method provided"));
        }
    }


    private Response authenticateWithEmailAndPassword(LoginRequest request) {

        try {
            authValidation.validateEmail(request.getEmail());
            authValidation.validatePassword(request.getPassword());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), null)
            );

            var user = repository.findByEmail(request.getEmail()).orElseThrow();


            updateLastLoginAndSave(user);
            createLoginRecord(user, true);

            var jwtToken = jwtService.generateToken(user);

            return buildResponse(user, jwtToken);

        } catch (AuthenticationException exception) {
            var user = repository.findByEmail(request.getEmail());

            // login failed
            user.ifPresent(value -> createLoginRecord(value, false));
            return new Response(new Response.Status(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
        }
    }


    private void updateLastLoginAndSave(User user) {
        user.setLastLoginTime(LocalDateTime.now());
        repository.save(user);
    }

    private void createLoginRecord(User user, Boolean loginStatus) {
        Logins login = Logins.builder()
                .user(user)
                .loginTime(LocalDateTime.now())
                .loginStatus(loginStatus)
                .build();

        loginsRepository.save(login);}

    private Response buildResponse(User user, String jwtToken) {
        var userResponse = LoginDTO
                .builder()
                .uuid(user.getUuid())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .profileUrl(user.getProfilePictureUrl())
                .role(user.getRole().name())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("user", userResponse);
        data.put("token", jwtToken);

        return new Response(new Response.Status(HttpStatus.OK.value(), "Authenticated Successfully"), data);
    }


    private void handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        if (isConstraintViolationForField(ex, "uk_buoitwamy4goeykc8n0r8b5jd")) {
            throw new IllegalArgumentException("Phone number is already registered. Please use a different phone number.");
        } else if (isConstraintViolationForField(ex, "uk_k11y3pdtsrjgy8w9b6q4bjwrx")) {
            throw new IllegalArgumentException("Email address is already registered. Please use a different email address.");
        }
    }

    private boolean isConstraintViolationForField(DataIntegrityViolationException ex, String fieldName) {
        return ex.getCause() != null &&
                ex.getCause() instanceof ConstraintViolationException &&
                Objects.requireNonNull(((ConstraintViolationException) ex.getCause()).getConstraintName()).contains(fieldName);
    }

    private static Object getClaimValue(String claimName, SignedJWT signedJwt) throws ParseException {
        JWTClaimsSet claimsSet = signedJwt.getJWTClaimsSet();

        Date expirationTime = claimsSet.getExpirationTime();
        Date now = new Date();
        if (expirationTime != null && expirationTime.before(now)) {
            throw new RuntimeException("Token has expired");
        }

        return claimsSet.getClaim(claimName);
    }


}