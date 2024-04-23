package com.example.amalisecuresail.config;


import com.example.amalisecuresail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Holds the application's manually defined beans
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    /**
     * Custom implementation of UserDetailsService.
     * Retrieves user details by email from the UserRepository.
     *
     * @return UserDetailsService implementation
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> repository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    /**
     * Configures and provides a custom AuthenticationProvider (DaoAuthenticationProvider).
     * Uses the custom UserDetailsService and a BCryptPasswordEncoder.
     *
     * @return AuthenticationProvider implementation
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configures and provides the AuthenticationManager using AuthenticationConfiguration.
     *
     * @param config AuthenticationConfiguration to obtain the AuthenticationManager
     * @return AuthenticationManager implementation
     * @throws Exception if there is an issue obtaining the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for password encoding.
     *
     * @return BCryptPasswordEncoder implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
