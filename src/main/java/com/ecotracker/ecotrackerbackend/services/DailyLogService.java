package com.ecotracker.ecotrackerbackend.services;

import com.ecotracker.ecotrackerbackend.entities.DailyLog;
import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.NotFoundException;
import com.ecotracker.ecotrackerbackend.repositories.DailyLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final UserService userService;

    // Spring inietta automaticamente le dipendenze tramite il costruttore
    public DailyLogService(DailyLogRepository dailyLogRepository, UserService userService) {
        this.dailyLogRepository = dailyLogRepository;
        this.userService = userService;
    }

    // GET BY ID: cerca un log per id, lancia NotFoundException se non esiste
    public DailyLog getById(UUID id) {
        return dailyLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Log con id " + id + " non trovato"));
    }

    // GET BY USER ID: ritorna tutti i log di un utente, usato per lo storico nella dashboard
    public List<DailyLog> getByUserId(UUID userId) {
        return dailyLogRepository.findByUserId(userId);
    }

    // GET BY USER ID AND DATE: ritorna tutti i log di un utente in una data specifica
    public List<DailyLog> getByUserIdAndDate(UUID userId, LocalDate date) {
        return dailyLogRepository.findByUserIdAndDate(userId, date);
    }

    // restituisce tutti i log di tutti gli utenti
    public List<DailyLog> getAll() {
        return dailyLogRepository.findAll();
    }

    // SAVE: crea un nuovo log giornaliero per l'utente con CO2 iniziale a 0
    public DailyLog save(UUID userId) {
        // recupero l'utente dal DB
        User user = userService.getById(userId);

        // creo un nuovo log per oggi
        DailyLog dailyLog = new DailyLog();
        dailyLog.setUser(user);
        dailyLog.setDate(LocalDate.now());
        dailyLog.setTotalCo2(0.0);

        return dailyLogRepository.save(dailyLog);
    }

    // UPDATE TOTAL CO2: aggiorna il totale di CO2 del log quando si aggiunge una nuova attività
    public DailyLog updateTotalCo2(UUID id, double co2ToAdd) {
        DailyLog found = this.getById(id);

        // aggiungo la co2 dell'attività appena inserita al totale del log
        found.setTotalCo2(found.getTotalCo2() + co2ToAdd);

        return dailyLogRepository.save(found);
    }

    // DELETE: elimina un log per id
    public void findByIdAndDelete(UUID id) {
        DailyLog found = this.getById(id);
        dailyLogRepository.delete(found);
    }

    // Calcola la media CO₂ giornaliera su tutti gli utenti per oggi
    public double getMediaGlobale() {
        LocalDate oggi = LocalDate.now();
        List<DailyLog> logsOggi = dailyLogRepository.findAll()
                .stream()
                .filter(log -> log.getDate().equals(oggi))
                .toList();
        if (logsOggi.isEmpty()) return 0;
        double totale = logsOggi.stream()
                .mapToDouble(DailyLog::getTotalCo2)
                .sum();
        return totale / logsOggi.size();
    }

    // Calcola la streak di giorni consecutivi con almeno un log per l'utente
    public java.util.Map<String, Object> getStreak(UUID userId) {
        List<DailyLog> logs = dailyLogRepository.findByUserId(userId);
        LocalDate oggi = LocalDate.now();

        java.util.Set<LocalDate> dateConLog = logs.stream()
                .map(DailyLog::getDate)
                .collect(java.util.stream.Collectors.toSet());

        // Calcoliamo la streak partendo da oggi
        int streak = 0;
        LocalDate giorno = oggi;
        while (dateConLog.contains(giorno)) {
            streak++;
            giorno = giorno.minusDays(1);
        }

        boolean oggiHaLog = dateConLog.contains(oggi);
        LocalDate ieri = oggi.minusDays(1);
        boolean atRisk = !oggiHaLog && dateConLog.contains(ieri) && streak > 0;

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("streak", streak);
        result.put("atRisk", atRisk);
        result.put("oggiHaLog", oggiHaLog);
        return result;
    }
}