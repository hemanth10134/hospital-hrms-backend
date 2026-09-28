package com.hospital.manpower.repository;

import com.hospital.manpower.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {

    List<Hospital> findByLocationId(UUID locationId);
}
