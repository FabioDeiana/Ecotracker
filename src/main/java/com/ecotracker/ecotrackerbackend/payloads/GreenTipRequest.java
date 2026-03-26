package com.ecotracker.ecotrackerbackend.payloads;

import com.ecotracker.ecotrackerbackend.entities.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GreenTipRequest {

    @NotBlank(message = "Il titolo è obbligatorio")
    private String title;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String description;

    private String titleEn;
    private String descriptionEn;

    @NotNull(message = "La categoria è obbligatoria")
    private ActivityType category;

    @Positive(message = "La stima di CO2 deve essere positiva")
    private Double co2SavedEstimate;

    public GreenTipRequest() {}

    public GreenTipRequest(String title, String description, ActivityType category, Double co2SavedEstimate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.co2SavedEstimate = co2SavedEstimate;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }

    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }

    public ActivityType getCategory() { return category; }
    public void setCategory(ActivityType category) { this.category = category; }

    public Double getCo2SavedEstimate() { return co2SavedEstimate; }
    public void setCo2SavedEstimate(Double co2SavedEstimate) { this.co2SavedEstimate = co2SavedEstimate; }
}
