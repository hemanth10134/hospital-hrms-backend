package com.hospital.manpower.service.impl;

import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.interfaces.IReferenceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceDataServiceImpl implements IReferenceDataService {

    private final OrganizationRepository organizationRepository;
    private final LocationRepository locationRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PlanningPeriodRepository planningPeriodRepository;
    private final ManpowerPlanMapper mapper;

    @Override
    public List<LookupDTO> getOrganizations() {
        return organizationRepository.findAll().stream().map(mapper::toLookup).toList();
    }

    @Override
    public List<LookupDTO> getLocations(UUID organizationId) {
        return locationRepository.findByOrganizationId(organizationId).stream().map(mapper::toLookup).toList();
    }

    @Override
    public List<LookupDTO> getHospitals(UUID locationId) {
        return hospitalRepository.findByLocationId(locationId).stream().map(mapper::toLookup).toList();
    }

    @Override
    public List<LookupDTO> getDepartments(UUID hospitalId) {
        return departmentRepository.findByHospitalId(hospitalId).stream().map(mapper::toLookup).toList();
    }

    @Override
    public List<LookupDTO> getDesignations(UUID organizationId) {
        return designationRepository.findByOrganizationId(organizationId).stream().map(mapper::toLookup).toList();
    }

    @Override
    public List<PlanningPeriodDTO> getPlanningPeriods() {
        return planningPeriodRepository.findAll().stream().map(mapper::toDto).toList();
    }
}
