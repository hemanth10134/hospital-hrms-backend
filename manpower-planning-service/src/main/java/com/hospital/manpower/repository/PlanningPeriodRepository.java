package com.hospital.manpower.repository;

import com.hospital.manpower.entity.PlanningPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanningPeriodRepository extends JpaRepository<PlanningPeriod, UUID> {
}
