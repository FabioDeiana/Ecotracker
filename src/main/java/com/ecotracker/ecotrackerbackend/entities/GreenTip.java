package com.ecotracker.ecotrackerbackend.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "green_tips")
public class GreenTip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ActivityType category;

    @Column(name = "co2_saved_estimate")
    private double co2SavedEstimate;

    public GreenTip() {}

    public GreenTip(String title, String description, ActivityType category, double co2SavedEstimate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.co2SavedEstimate = co2SavedEstimate;
    }

    public UUID getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ActivityType getCategory() { return category; }
    public void setCategory(ActivityType category) { this.category = category; }

    public double getCo2SavedEstimate() { return co2SavedEstimate; }
    public void setCo2SavedEstimate(double co2SavedEstimate) { this.co2SavedEstimate = co2SavedEstimate; }
}
