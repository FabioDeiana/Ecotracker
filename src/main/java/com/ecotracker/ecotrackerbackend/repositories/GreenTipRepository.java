package com.ecotracker.ecotrackerbackend.repositories;

import com.ecotracker.ecotrackerbackend.entities.ActivityType;
import com.ecotracker.ecotrackerbackend.entities.GreenTip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GreenTipRepository extends JpaRepository<GreenTip, UUID> {
    List<GreenTip> findByCategory(ActivityType category);
}