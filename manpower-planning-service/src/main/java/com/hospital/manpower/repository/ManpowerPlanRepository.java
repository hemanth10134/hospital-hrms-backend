package com.hospital.manpower.repository;

import com.hospital.manpower.entity.ManpowerPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManpowerPlanRepository extends JpaRepository<ManpowerPlan, UUID> {

    List<ManpowerPlan> findByHospitalIdAndDepartmentIdOrderByCreatedAtDesc(UUID hospitalId, UUID departmentId);

    Optional<ManpowerPlan> findByHospitalIdAndDepartmentIdAndPlanningPeriodId(
            UUID hospitalId, UUID departmentId, UUID planningPeriodId);
}
