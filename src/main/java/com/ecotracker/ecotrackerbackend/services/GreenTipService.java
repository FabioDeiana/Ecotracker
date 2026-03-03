package com.ecotracker.ecotrackerbackend.services;

import com.ecotracker.ecotrackerbackend.entities.ActivityType;
import com.ecotracker.ecotrackerbackend.entities.GreenTip;
import com.ecotracker.ecotrackerbackend.exceptions.NotFoundException;
import com.ecotracker.ecotrackerbackend.payloads.GreenTipRequest;
import com.ecotracker.ecotrackerbackend.repositories.GreenTipRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GreenTipService {

    private final GreenTipRepository greenTipRepository;

    // Spring inietta automaticamente le dipendenze tramite il costruttore
    public GreenTipService(GreenTipRepository greenTipRepository) {
        this.greenTipRepository = greenTipRepository;
    }

    // GET ALL: ritorna tutti i consigli verdi
    public List<GreenTip> getAll() {
        return greenTipRepository.findAll();
    }

    // GET BY CATEGORY: ritorna i consigli filtrati per categoria
    // usato per suggerire consigli in base alle attività dell'utente
    public List<GreenTip> getByCategory(ActivityType category) {
        return greenTipRepository.findByCategory(category);
    }

    // SAVE: crea un nuovo consiglio verde (solo admin)
    public GreenTip save(GreenTipRequest payload) {
        GreenTip greenTip = new GreenTip();
        greenTip.setTitle(payload.getTitle());
        greenTip.setDescription(payload.getDescription());
        greenTip.setCategory(payload.getCategory());
        greenTip.setCo2SavedEstimate(payload.getCo2SavedEstimate());

        return greenTipRepository.save(greenTip);
    }

    // UPDATE: aggiorna un consiglio verde esistente (solo admin)
    public GreenTip findByIdAndUpdate(UUID id, GreenTipRequest payload) {
        GreenTip found = greenTipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consiglio con id " + id + " non trovato"));

        found.setTitle(payload.getTitle());
        found.setDescription(payload.getDescription());
        found.setCategory(payload.getCategory());
        found.setCo2SavedEstimate(payload.getCo2SavedEstimate());

        return greenTipRepository.save(found);
    }

    // DELETE: elimina un consiglio verde (solo admin)
    public void findByIdAndDelete(UUID id) {
        GreenTip found = greenTipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consiglio con id " + id + " non trovato"));
        greenTipRepository.delete(found);
    }
}
