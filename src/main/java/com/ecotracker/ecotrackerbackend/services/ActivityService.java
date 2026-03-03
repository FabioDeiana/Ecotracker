package com.ecotracker.ecotrackerbackend.services;

import com.ecotracker.ecotrackerbackend.entities.Activity;
import com.ecotracker.ecotrackerbackend.entities.ActivityType;
import com.ecotracker.ecotrackerbackend.entities.DailyLog;
import com.ecotracker.ecotrackerbackend.exceptions.NotFoundException;
import com.ecotracker.ecotrackerbackend.payloads.ActivityRequest;
import com.ecotracker.ecotrackerbackend.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final DailyLogService dailyLogService;

    @Value("${climatiq.api.key}")
    private String climatiqApiKey;

    // activity_id di Climatiq per ogni tipo di attività
    private static final Map<ActivityType, String> ACTIVITY_IDS = Map.of(
            ActivityType.CAR, "passenger_vehicle-vehicle_type_car-fuel_source_petrol-engine_size_na-vehicle_age_na-vehicle_weight_na",
            ActivityType.MEAT, "food-type_beef_dishes",
            ActivityType.ELECTRICITY, "real_estate_energy_consumption-type_single_family_home",
            ActivityType.FLIGHT, "passenger_flight-route_type_outside_uk-aircraft_type_na-distance_na-class_na-rf_included-distance_uplift_included",
            ActivityType.HEATING, "fuel-type_natural_gas_net-fuel_use_na"
    );

    // unità di misura per ogni tipo di attività
    private static final Map<ActivityType, String> ACTIVITY_UNITS = Map.of(
            ActivityType.CAR, "km",
            ActivityType.MEAT, "kg",
            ActivityType.ELECTRICITY, "kWh",
            ActivityType.FLIGHT, "passenger-km",
            ActivityType.HEATING, "kWh"
    );

    // Spring inietta automaticamente le dipendenze tramite il costruttore
    public ActivityService(ActivityRepository activityRepository, DailyLogService dailyLogService) {
        this.activityRepository = activityRepository;
        this.dailyLogService = dailyLogService;
    }

    // chiama l'API di Climatiq e restituisce la CO2 calcolata in kg
    private double calculateCo2(ActivityType type, double value) {
        // costruisco il body della richiesta
        Map<String, Object> requestBody = Map.of(
                "emission_factor", Map.of(
                        "activity_id", ACTIVITY_IDS.get(type)
                ),
                "parameters", Map.of(
                        "value", value,
                        "value_unit", ACTIVITY_UNITS.get(type)
                )
        );

        // chiamo l'API di Climatiq
        Map response = RestClient.create()
                .post()
                .uri("https://api.climatiq.io/data/v1/estimate")
                .header("Authorization", "Bearer " + climatiqApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        // estraggo il valore di CO2 dalla risposta
        return ((Number) response.get("co2e")).doubleValue();
    }

    // SAVE: crea una nuova attività, calcola la CO2 e aggiorna il totale del log
    public Activity save(ActivityRequest payload, UUID dailyLogId) {
        // recupero il log dal DB
        DailyLog dailyLog = dailyLogService.getById(dailyLogId);

        // calcolo la CO2 chiamando Climatiq
        double co2 = calculateCo2(payload.getType(), payload.getValue());

        // creo e salvo l'attività
        Activity activity = new Activity();
        activity.setType(payload.getType());
        activity.setValue(payload.getValue());
        activity.setCo2Emission(co2);
        activity.setDailyLog(dailyLog);

        Activity saved = activityRepository.save(activity);

        // aggiorno il totale CO2 del log
        dailyLogService.updateTotalCo2(dailyLogId, co2);

        return saved;
    }

    // GET BY DAILY LOG ID: ritorna tutte le attività di un log specifico
    public List<Activity> getByDailyLogId(UUID dailyLogId) {
        return activityRepository.findByDailyLogId(dailyLogId);
    }

    // DELETE: elimina un'attività e sottrae la sua CO2 dal totale del log
    public void findByIdAndDelete(UUID id) {
        Activity found = activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attività con id " + id + " non trovata"));

        // sottraggo la CO2 di questa attività dal totale del log
        dailyLogService.updateTotalCo2(found.getDailyLog().getId(), -found.getCo2Emission());

        activityRepository.delete(found);
    }
}
