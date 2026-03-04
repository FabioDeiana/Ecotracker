package com.ecotracker.ecotrackerbackend.controllers;

import com.ecotracker.ecotrackerbackend.entities.ActivityType;
import com.ecotracker.ecotrackerbackend.entities.GreenTip;
import com.ecotracker.ecotrackerbackend.exceptions.ValidationException;
import com.ecotracker.ecotrackerbackend.payloads.GreenTipRequest;
import com.ecotracker.ecotrackerbackend.services.GreenTipService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tips")
public class GreenTipController {

    private final GreenTipService greenTipService;

    // Spring inietta automaticamente le dipendenze tramite il costruttore
    public GreenTipController(GreenTipService greenTipService) {
        this.greenTipService = greenTipService;
    }

    // GET /tips - ritorna tutti i consigli verdi
    @GetMapping
    public List<GreenTip> getAll() {
        return greenTipService.getAll();
    }

    // GET /tips/category?category=CAR - ritorna i consigli per categoria
    // usato per suggerire consigli in base alle attività dell'utente
    @GetMapping("/category")
    public List<GreenTip> getByCategory(@RequestParam ActivityType category) {
        return greenTipService.getByCategory(category);
    }

    // POST /tips - crea un nuovo consiglio verde (solo ADMIN)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public GreenTip create(@RequestBody @Validated GreenTipRequest payload,
                           BindingResult validateResult) {
        // controllo errori di validazione
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        return greenTipService.save(payload);
    }

    // PUT /tips/{tipId} - aggiorna un consiglio verde (solo ADMIN)
    @PutMapping("/{tipId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public GreenTip update(@PathVariable UUID tipId,
                           @RequestBody @Validated GreenTipRequest payload,
                           BindingResult validateResult) {
        // controllo errori di validazione
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        return greenTipService.findByIdAndUpdate(tipId, payload);
    }

    // DELETE /tips/{tipId} - elimina un consiglio verde (solo ADMIN)
    @DeleteMapping("/{tipId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID tipId) {
        greenTipService.findByIdAndDelete(tipId);
    }
}
