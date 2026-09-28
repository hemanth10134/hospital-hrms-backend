package com.hospital.manpower.repository;

import com.hospital.manpower.entity.PositionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PositionRequestRepository extends JpaRepository<PositionRequest, UUID> {

    List<PositionRequest> findByDepartmentIdOrderByCreatedAtDesc(UUID departmentId);
}
