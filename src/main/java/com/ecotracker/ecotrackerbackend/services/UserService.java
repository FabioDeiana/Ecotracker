package com.ecotracker.ecotrackerbackend.services;

import com.ecotracker.ecotrackerbackend.entities.Role;
import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.NotFoundException;
import com.ecotracker.ecotrackerbackend.payloads.RegisterRequest;
import com.ecotracker.ecotrackerbackend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
    }

    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User save(RegisterRequest payload) {
        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email già in uso");
        }

        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        user.setPassword(passwordEncoder.encode(payload.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User findByIdAndUpdate(UUID id, RegisterRequest payload) {
        User found = this.getById(id);

        if (!found.getEmail().equals(payload.getEmail())
                && userRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email già in uso");
        }

        found.setName(payload.getName());
        found.setEmail(payload.getEmail());
        found.setPassword(passwordEncoder.encode(payload.getPassword()));

        return userRepository.save(found);
    }
    // find user and delete it
    public void findByIdAndDelete(UUID id) {
        User found = this.getById(id);
        userRepository.delete(found);
    }
}
