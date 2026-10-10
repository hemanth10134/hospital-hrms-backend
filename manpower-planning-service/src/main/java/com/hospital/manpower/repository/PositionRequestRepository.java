package com.hospital.manpower.repository;

import com.hospital.manpower.entity.PositionRequest;
import com.hospital.manpower.enums.PositionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PositionRequestRepository extends JpaRepository<PositionRequest, UUID> {

    List<PositionRequest> findByDepartmentIdOrderByCreatedAtDesc(UUID departmentId);

    boolean existsByDepartmentIdAndDesignationIdAndStatusIn(
            UUID departmentId, UUID designationId, Collection<PositionRequestStatus> statuses);

    List<PositionRequest> findByDepartmentIdAndStatusNot(UUID departmentId, PositionRequestStatus status);

    List<PositionRequest> findByStatusNot(PositionRequestStatus status);
}
