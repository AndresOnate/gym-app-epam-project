package com.epam.gymapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.dto.PasswordChangeRequest;
import com.epam.gymapp.service.UserService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final UserService userService;
    private final Counter passwordChangeCounter;

    @Autowired
    public AccountController(UserService userService, MeterRegistry meterRegistry) {
        this.userService = userService;
        this.passwordChangeCounter = meterRegistry.counter("password_changes_total", "action", "change");
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        userService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
        passwordChangeCounter.increment(); 
        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }
}
    
