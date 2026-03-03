package com.ecotracker.ecotrackerbackend.services;

import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.UnauthorizedException;
import com.ecotracker.ecotrackerbackend.payloads.LoginRequest;
import com.ecotracker.ecotrackerbackend.payloads.RegisterRequest;
import com.ecotracker.ecotrackerbackend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }
// gestione login
    public String login(LoginRequest payload) {
        // cerco l'utente per email, se non esiste findByEmail lancia NotFoundException
        User user = userService.findByEmail(payload.getEmail());

        // confronto la password inserita con quella criptata nel DB
        // non possiamo confrontarle direttamente perché nel DB è criptata
        if (!passwordEncoder.matches(payload.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenziali non valide");
        }

        // se tutto ok genero e restituisco il token JWT
        return jwtUtils.generateToken(user);
    }
    // REGISTER: delega la registrazione a UserService
    public User register(RegisterRequest payload) {
        return userService.save(payload);
    }
}