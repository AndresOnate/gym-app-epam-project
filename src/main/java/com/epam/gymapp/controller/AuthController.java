package com.epam.gymapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import com.epam.gymapp.dto.RegistrationDto;
import com.epam.gymapp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final Counter loginCounter;

    @Autowired
    public AuthController(UserService userService, MeterRegistry meterRegistry) {
        this.userService = userService;
        this.loginCounter = meterRegistry.counter("auth_logins_total", "action", "login");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody RegistrationDto registrationDto) {
        loginCounter.increment(); // Incrementa el contador cada vez que se hace un login
        return ResponseEntity.ok(userService.authenticate(registrationDto.getUsername(), registrationDto.getPassword()));
    }
}