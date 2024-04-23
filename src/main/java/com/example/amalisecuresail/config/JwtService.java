package com.example.amalisecuresail.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JSON Web Token (JWT) generation, validation, and extraction.
 */
@Service
public class JwtService {
    // The secret key used for signing and verifying JWTs
    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token from which to extract the username
     * @return The username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    /**
     * Generates a JWT token for a given UserDetails.
     *
     * @param userDetails The UserDetails for which to generate the token
     * @return The generated JWT token
     */
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims for a given UserDetails.
     *
     * @param extraClaims   Additional claims to include in the token
     * @param userDetails   The UserDetails for which to generate the token
     * @return The generated JWT token
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails

    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates whether a given JWT token is valid for a specific UserDetails.
     *
     * @param token       The JWT token to validate
     * @param userDetails The UserDetails against which to validate the token
     * @return True if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername((token));
        return (username.equals(userDetails.getUsername())) &&!isTokenExpired(token);
    }

    /**
     * checks if JWT is expired
     * @param token JWt to be checked
     * @return JWT is expired ? True : False
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * extracts expiration date claim registered in the payload section
     * @param token token to extract username claim from
     * @return username extracted
     */
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * extract claim based on {@link io.jsonwebtoken.Claims} function passed to it
     * @param token JWt to extract claim from
     * @param claimsResolves claim type function
     * @return claim extracted
     * @param <T> data type of claim extracted
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolves){
        final Claims claims = extractAllClaims(token);
        return claimsResolves.apply(claims);
    }

    /**
     * extracts all claims from the JWT
     * @param token JWT sent
     * @return claims extracted
     */
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();

    }

    /**
     * decodes secret key and hashes to make it suitable for the JWT token generation
     * algorithm
     * @return hashed secret key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}