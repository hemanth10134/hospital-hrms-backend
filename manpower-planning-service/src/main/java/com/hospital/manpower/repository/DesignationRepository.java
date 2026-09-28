package com.hospital.manpower.repository;

import com.hospital.manpower.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DesignationRepository extends JpaRepository<Designation, UUID> {

    List<Designation> findByOrganizationId(UUID organizationId);
}
