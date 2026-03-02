package com.ecotracker.ecotrackerbackend.repositories;

import com.ecotracker.ecotrackerbackend.entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findByDailyLogId(UUID dailyLogId);
}