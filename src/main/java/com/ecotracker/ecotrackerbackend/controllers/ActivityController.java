package com.ecotracker.ecotrackerbackend.controllers;

import com.ecotracker.ecotrackerbackend.entities.Activity;
import com.ecotracker.ecotrackerbackend.entities.DailyLog;
import com.ecotracker.ecotrackerbackend.entities.User;
import com.ecotracker.ecotrackerbackend.exceptions.UnauthorizedException;
import com.ecotracker.ecotrackerbackend.exceptions.ValidationException;
import com.ecotracker.ecotrackerbackend.payloads.ActivityRequest;
import com.ecotracker.ecotrackerbackend.services.ActivityService;
import com.ecotracker.ecotrackerbackend.services.DailyLogService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/logs/{logId}/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final DailyLogService dailyLogService;

    public ActivityController(ActivityService activityService, DailyLogService dailyLogService) {
        this.activityService = activityService;
        this.dailyLogService = dailyLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Activity create(@PathVariable UUID logId,
                           @RequestBody @Validated ActivityRequest payload,
                           BindingResult validateResult,
                           @AuthenticationPrincipal User currentUser) {
        DailyLog log = dailyLogService.getById(logId);
        if (!log.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non sei autorizzato ad aggiungere attività a questo log");
        }
        if (validateResult.hasErrors()) {
            List<String> errorList = validateResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorList);
        }
        return activityService.save(payload, logId);
    }

    @GetMapping
    public List<Activity> getByLogId(@PathVariable UUID logId,
                                     @AuthenticationPrincipal User currentUser) {
        DailyLog log = dailyLogService.getById(logId);
        if (!log.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non sei autorizzato a vedere le attività di questo log");
        }
        return activityService.getByDailyLogId(logId);
    }

    @DeleteMapping("/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID logId,
                       @PathVariable UUID activityId,
                       @AuthenticationPrincipal User currentUser) {
        DailyLog log = dailyLogService.getById(logId);
        if (!log.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Non sei autorizzato a eliminare questa attività");
        }
        activityService.findByIdAndDelete(activityId);
    }
}