package com.hospital.manpower.service.impl;

import com.hospital.manpower.constants.ErrorMessages;
import com.hospital.manpower.dto.request.CreateDesignationRequest;
import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;
import com.hospital.manpower.entity.Designation;
import com.hospital.manpower.entity.Organization;
import com.hospital.manpower.exception.DesignationAlreadyExistsException;
import com.hospital.manpower.exception.ResourceNotFoundException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.interfaces.IReferenceDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceDataServiceImpl implements IReferenceDataService {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataServiceImpl.class);

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
    @Transactional
    public LookupDTO createDesignation(CreateDesignationRequest request) {
        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.ORGANIZATION_NOT_FOUND, request.organizationId())));

        if (designationRepository.existsByOrganizationIdAndCode(request.organizationId(), request.code())) {
            throw new DesignationAlreadyExistsException(
                    String.format(ErrorMessages.DESIGNATION_CODE_ALREADY_EXISTS, request.code()));
        }

        Designation designation = new Designation();
        designation.setOrganization(organization);
        designation.setCode(request.code());
        designation.setName(request.name());

        Designation saved = designationRepository.save(designation);
        log.info("Created designation {} ({}) for organization {}", saved.getName(), saved.getCode(), organization.getId());
        return mapper.toLookup(saved);
    }

    @Override
    public List<PlanningPeriodDTO> getPlanningPeriods() {
        return planningPeriodRepository.findAll().stream().map(mapper::toDto).toList();
    }
}
