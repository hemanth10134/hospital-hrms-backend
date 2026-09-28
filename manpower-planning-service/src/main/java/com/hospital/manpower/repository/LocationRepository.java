package com.hospital.manpower.repository;

import com.hospital.manpower.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findByOrganizationId(UUID organizationId);
}
