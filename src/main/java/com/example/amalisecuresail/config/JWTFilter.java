package com.example.amalisecuresail.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Determines how the authentication request of a given user will be processed.
 * Extends {@link org.springframework.web.filter.OncePerRequestFilter} class
 */
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {



    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * intercepts {@link jakarta.servlet.http.HttpServletRequest} instance and applies a custom JWTFilter
     * @param request a {@link jakarta.servlet.http.HttpServletRequest} instance
     * @param response a {@link jakarta.servlet.http.HttpServletResponse} instance
     * @param filterChain instance that implements the filter on the request and response instances
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /* Extract Json Web Token from request header */
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authorizationHeader.substring(7);

        /* Extract user from JWT payload section */
        userName = jwtService.extractUsername(jwt);

        /* Authenticates a user when they get a token generated for them on sign up */
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}