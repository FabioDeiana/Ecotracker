package com.ecotracker.ecotrackerbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Column(nullable = false)
    private double value;

    @Column(name = "co2_emission", nullable = false)
    private double co2Emission;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    public Activity() {}

    public Activity(ActivityType type, double value, double co2Emission, DailyLog dailyLog) {
        this.type = type;
        this.value = value;
        this.co2Emission = co2Emission;
        this.dailyLog = dailyLog;
    }

    public UUID getId() { return id; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public double getCo2Emission() { return co2Emission; }
    public void setCo2Emission(double co2Emission) { this.co2Emission = co2Emission; }

    public DailyLog getDailyLog() { return dailyLog; }
    public void setDailyLog(DailyLog dailyLog) { this.dailyLog = dailyLog; }
}