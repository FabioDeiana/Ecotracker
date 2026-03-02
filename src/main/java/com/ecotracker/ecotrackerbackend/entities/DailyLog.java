package com.ecotracker.ecotrackerbackend.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_logs")
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "total_co2", nullable = false)
    private double totalCo2;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL)
    private List<Activity> activities;

    public DailyLog() {}

    public DailyLog(LocalDate date, double totalCo2, User user) {
        this.date = date;
        this.totalCo2 = totalCo2;
        this.user = user;
    }

    public UUID getId() { return id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getTotalCo2() { return totalCo2; }
    public void setTotalCo2(double totalCo2) { this.totalCo2 = totalCo2; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Activity> getActivities() { return activities; }
    public void setActivities(List<Activity> activities) { this.activities = activities; }
}