package com.ecotracker.ecotrackerbackend.controllers;

import com.ecotracker.ecotrackerbackend.exceptions.ValidationException;
import com.ecotracker.ecotrackerbackend.payloads.AuthResponse;
import com.ecotracker.ecotrackerbackend.payloads.LoginRequest;
import com.ecotracker.ecotrackerbackend.payloads.RegisterRequest;
import com.ecotracker.ecotrackerbackend.services.AuthService;
import com.ecotracker.ecotrackerbackend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // Spring inietta automaticamente le dipendenze tramite il costruttore
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // POST /auth/register - registra un nuovo utente
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@RequestBody @Validated RegisterRequest payload, BindingResult validateResult) {
        // controllo se ci sono errori di validazione
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        userService.save(payload);
        return "Registrazione avvenuta con successo";
    }

    // POST /auth/login - esegue il login e restituisce il token JWT
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Validated LoginRequest payload, BindingResult validateResult) {
        // controllo se ci sono errori di validazione
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        // restituisco il token JWT dentro un AuthResponse
        return new AuthResponse(authService.login(payload));
    }
}
