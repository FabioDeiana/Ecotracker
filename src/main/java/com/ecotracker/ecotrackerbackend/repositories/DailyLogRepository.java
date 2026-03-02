package com.ecotracker.ecotrackerbackend.repositories;

import com.ecotracker.ecotrackerbackend.entities.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyLogRepository extends JpaRepository<DailyLog, UUID> {
    List<DailyLog> findByUserId(UUID userId);
    List<DailyLog> findByUserIdAndDate(UUID userId, LocalDate date);
}
