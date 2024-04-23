package com.example.amalisecuresail.controller;


import com.example.amalisecuresail.dto.Response;
import com.example.amalisecuresail.payload.LoginRequest;
import com.example.amalisecuresail.payload.RegisterRequest;
import com.example.amalisecuresail.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing account creation and authentication
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;


    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @RequestBody RegisterRequest request
    ) {
        Response response = service.register(request);
        return ResponseEntity.status(response.getStatus().getCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> authenticate(@RequestBody LoginRequest request) {
        Response response = service.authenticate(request);
        return ResponseEntity.status(response.getStatus().getCode()).body(response);
    }

}
