package com.ecotracker.ecotrackerbackend.controllers;

import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.ValidationException;
import com.ecotracker.ecotrackerbackend.payloads.RegisterRequest;
import com.ecotracker.ecotrackerbackend.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Spring inietta automaticamente le dipendenze tramite il costruttore
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users - ritorna tutti gli utenti paginati (solo ADMIN)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<User> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userService.getAll(pageable);
    }

    // GET /users/me - ritorna l'utente autenticato corrente
    @GetMapping("/me")
    public User getMe(@AuthenticationPrincipal User currentUser) {
        // Spring inietta direttamente l'utente autenticato dal SecurityContext
        return currentUser;
    }

    // PUT /users/me - lo user aggiorna il proprio profilo
    @PutMapping("/me")
    public User updateMe(@AuthenticationPrincipal User currentUser,
                         @RequestBody @Validated RegisterRequest payload,
                         BindingResult validateResult) {
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        return userService.findByIdAndUpdate(currentUser.getId(), payload);
    }

    // DELETE /users/me - lo user elimina il proprio account
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal User currentUser) {
        userService.findByIdAndDelete(currentUser.getId());
    }

    // GET /users/{userId} - ritorna un utente per id (solo ADMIN)
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getById(@PathVariable UUID userId) {
        return userService.getById(userId);
    }

    // PUT /users/{userId} - aggiorna un utente (solo ADMIN)
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User update(@PathVariable UUID userId,
                       @RequestBody @Validated RegisterRequest payload,
                       BindingResult validateResult) {
        // controllo se ci sono errori di validazione
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        return userService.findByIdAndUpdate(userId, payload);
    }

    // DELETE /users/{userId} - elimina un utente (solo ADMIN)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID userId) {
        userService.findByIdAndDelete(userId);
    }

    // PATCH /users/me/avatar - aggiorna l'avatar dell'utente loggato
    @PatchMapping("/me/avatar")
    public User updateAvatar(@AuthenticationPrincipal User currentUser,
                             @RequestParam("file") MultipartFile file) {
        return userService.updateAvatar(currentUser.getId(), file);
    }
}