package com.hospital.manpower.repository;

import com.hospital.manpower.entity.ManpowerPlanDesignation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ManpowerPlanDesignationRepository extends JpaRepository<ManpowerPlanDesignation, UUID> {

    Optional<ManpowerPlanDesignation> findByIdAndPlanId(UUID id, UUID planId);
}
