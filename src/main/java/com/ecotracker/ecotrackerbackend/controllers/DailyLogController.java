package com.ecotracker.ecotrackerbackend.controllers;

import com.ecotracker.ecotrackerbackend.entities.DailyLog;
import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.UnauthorizedException;
import com.ecotracker.ecotrackerbackend.services.DailyLogService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/logs")
public class DailyLogController {

    private final DailyLogService dailyLogService;

    public DailyLogController(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DailyLog create(@AuthenticationPrincipal User currentUser) {
        return dailyLogService.save(currentUser.getId());
    }

    @GetMapping("/me")
    public List<DailyLog> getMyLogs(@AuthenticationPrincipal User currentUser) {
        return dailyLogService.getByUserId(currentUser.getId());
    }

    @GetMapping("/me/today")
    public List<DailyLog> getMyTodayLogs(@AuthenticationPrincipal User currentUser) {
        return dailyLogService.getByUserIdAndDate(currentUser.getId(), LocalDate.now());
    }

    @GetMapping("/{logId}")
    public DailyLog getById(@PathVariable UUID logId,
                            @AuthenticationPrincipal User currentUser) {
        DailyLog log = dailyLogService.getById(logId);
        if (!log.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non sei autorizzato a vedere questo log");
        }
        return log;
    }

    // GET /logs/all - ritorna tutti i log di tutti gli utenti (solo ADMIN)
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<DailyLog> getAll() {
        return dailyLogService.getAll();
    }

    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID logId,
                       @AuthenticationPrincipal User currentUser) {
        DailyLog log = dailyLogService.getById(logId);
        if (!log.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non sei autorizzato a eliminare questo log");
        }
        dailyLogService.findByIdAndDelete(logId);
    }

    // GET /logs/stats/media — restituisce la media CO₂ giornaliera globale
    @GetMapping("/stats/media")
    public double getMediaGlobale() {
        return dailyLogService.getMediaGlobale();
    }
}