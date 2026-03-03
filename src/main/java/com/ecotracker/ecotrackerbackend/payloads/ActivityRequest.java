package com.ecotracker.ecotrackerbackend.payloads;

import com.ecotracker.ecotrackerbackend.entities.ActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ActivityRequest {

    @NotNull(message = "Il tipo di attività è obbligatorio")
    private ActivityType type;

    @NotNull(message = "Il valore è obbligatorio")
    @Positive(message = "Il valore deve essere positivo")
    private Double value;

    public ActivityRequest() {}

    public ActivityRequest(ActivityType type, Double value) {
        this.type = type;
        this.value = value;
    }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}
