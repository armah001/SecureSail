package com.example.amalisecuresail.util;

import com.example.amalisecuresail.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component

public class AuthenticationHelpers {
    public static Optional<User> getAuthenticatedUserWithoutErrorHandling() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == "anonymousUser")
            return Optional.empty();
        return Optional.of((User) authentication.getPrincipal());
    }

    public static User getAuthenticatedUserWithErrorHandling(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof String || !authentication.isAuthenticated())
            throw new IllegalArgumentException("You are not authenticated!!! Please login.");

        return (User) authentication.getPrincipal();
    }
}
