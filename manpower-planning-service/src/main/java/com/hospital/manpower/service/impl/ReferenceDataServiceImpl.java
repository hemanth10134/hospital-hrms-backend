package com.hospital.manpower.service.impl;

import com.hospital.manpower.constants.ErrorMessages;
import com.hospital.manpower.dto.request.CreateDesignationRequest;
import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;
import com.hospital.manpower.dto.response.ReferenceHierarchyDTO;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.exception.DuplicateReferenceDataException;
import com.hospital.manpower.exception.ResourceNotFoundException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.interfaces.IReferenceDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceDataServiceImpl implements IReferenceDataService {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataServiceImpl.class);
    private static final int MAX_CODE_LENGTH = 30;

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
        Organization organization = getOrganization(request.organizationId());

        if (designationRepository.existsByOrganizationIdAndCode(request.organizationId(), request.code())) {
            throw new DuplicateReferenceDataException(
                    String.format(ErrorMessages.DESIGNATION_CODE_ALREADY_EXISTS, request.code()));
        }
        if (designationRepository.existsByOrganizationIdAndNameIgnoreCase(request.organizationId(), request.name().trim())) {
            throw new DuplicateReferenceDataException(
                    String.format(ErrorMessages.DESIGNATION_NAME_ALREADY_EXISTS, request.name().trim()));
        }

        Designation designation = new Designation();
        designation.setOrganization(organization);
        designation.setCode(request.code());
        designation.setName(request.name().trim());

        Designation saved = designationRepository.save(designation);
        log.info("Created designation {} ({}) for organization {}", saved.getName(), saved.getCode(), organization.getId());
        return mapper.toLookup(saved);
    }

    @Override
    @Transactional
    public LookupDTO createLocation(UUID organizationId, String name) {
        Organization organization = getOrganization(organizationId);
        String trimmedName = name.trim();
        if (locationRepository.existsByOrganizationIdAndNameIgnoreCase(organizationId, trimmedName)) {
            throw new DuplicateReferenceDataException(String.format(ErrorMessages.LOCATION_ALREADY_EXISTS, trimmedName));
        }

        Location location = new Location();
        location.setOrganization(organization);
        location.setName(trimmedName);
        location.setCode(uniqueCode(trimmedName, code -> locationRepository.existsByOrganizationIdAndCode(organizationId, code)));

        Location saved = locationRepository.save(location);
        log.info("Created location {} for organization {}", saved.getName(), organizationId);
        return mapper.toLookup(saved);
    }

    @Override
    @Transactional
    public LookupDTO createHospital(UUID locationId, String name) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.LOCATION_NOT_FOUND, locationId)));
        String trimmedName = name.trim();
        if (hospitalRepository.existsByLocationIdAndNameIgnoreCase(locationId, trimmedName)) {
            throw new DuplicateReferenceDataException(String.format(ErrorMessages.HOSPITAL_ALREADY_EXISTS, trimmedName));
        }

        Hospital hospital = new Hospital();
        hospital.setLocation(location);
        hospital.setName(trimmedName);
        hospital.setCode(uniqueCode(trimmedName, code -> hospitalRepository.existsByLocationIdAndCode(locationId, code)));

        Hospital saved = hospitalRepository.save(hospital);
        log.info("Created hospital {} for location {}", saved.getName(), locationId);
        return mapper.toLookup(saved);
    }

    @Override
    @Transactional
    public LookupDTO createDepartment(UUID hospitalId, String name) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.HOSPITAL_NOT_FOUND, hospitalId)));
        String trimmedName = name.trim();
        if (departmentRepository.existsByHospitalIdAndNameIgnoreCase(hospitalId, trimmedName)) {
            throw new DuplicateReferenceDataException(String.format(ErrorMessages.DEPARTMENT_ALREADY_EXISTS, trimmedName));
        }

        Department department = new Department();
        department.setHospital(hospital);
        department.setName(trimmedName);
        department.setCode(uniqueCode(trimmedName, code -> departmentRepository.existsByHospitalIdAndCode(hospitalId, code)));

        Department saved = departmentRepository.save(department);
        log.info("Created department {} for hospital {}", saved.getName(), hospitalId);
        return mapper.toLookup(saved);
    }

    @Override
    public List<ReferenceHierarchyDTO> getHierarchy() {
        Comparator<LookupDTO> byName = Comparator.comparing(LookupDTO::name, String.CASE_INSENSITIVE_ORDER);

        Map<UUID, List<LookupDTO>> departmentsByHospital = departmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(department -> department.getHospital().getId(),
                        Collectors.mapping(mapper::toLookup, Collectors.toList())));
        Map<UUID, List<Hospital>> hospitalsByLocation = hospitalRepository.findAll().stream()
                .collect(Collectors.groupingBy(hospital -> hospital.getLocation().getId()));
        Map<UUID, List<Location>> locationsByOrganization = locationRepository.findAll().stream()
                .collect(Collectors.groupingBy(location -> location.getOrganization().getId()));
        Map<UUID, List<LookupDTO>> designationsByOrganization = designationRepository.findAll().stream()
                .collect(Collectors.groupingBy(designation -> designation.getOrganization().getId(),
                        Collectors.mapping(mapper::toLookup, Collectors.toList())));

        return organizationRepository.findAll().stream()
                .map(organization -> new ReferenceHierarchyDTO(
                        organization.getId(),
                        organization.getCode(),
                        organization.getName(),
                        designationsByOrganization.getOrDefault(organization.getId(), List.of()).stream().sorted(byName).toList(),
                        locationsByOrganization.getOrDefault(organization.getId(), List.of()).stream()
                                .map(location -> new ReferenceHierarchyDTO.LocationNode(
                                        location.getId(), location.getCode(), location.getName(),
                                        hospitalsByLocation.getOrDefault(location.getId(), List.of()).stream()
                                                .map(hospital -> new ReferenceHierarchyDTO.HospitalNode(
                                                        hospital.getId(), hospital.getCode(), hospital.getName(),
                                                        departmentsByHospital.getOrDefault(hospital.getId(), List.of())
                                                                .stream().sorted(byName).toList()))
                                                .sorted(Comparator.comparing(ReferenceHierarchyDTO.HospitalNode::name, String.CASE_INSENSITIVE_ORDER))
                                                .toList()))
                                .sorted(Comparator.comparing(ReferenceHierarchyDTO.LocationNode::name, String.CASE_INSENSITIVE_ORDER))
                                .toList()))
                .toList();
    }

    @Override
    public List<PlanningPeriodDTO> getPlanningPeriods() {
        return planningPeriodRepository.findAll().stream().map(mapper::toDto).toList();
    }

    private Organization getOrganization(UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.ORGANIZATION_NOT_FOUND, organizationId)));
    }

    /**
     * Derives an upper-case code from a display name ("West Delhi" -> "WEST-DELHI"),
     * suffixing -2, -3… when two different names slug to the same code.
     */
    static String uniqueCode(String name, Predicate<String> codeExists) {
        String base = name.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "-").replaceAll("^-|-$", "");
        if (base.isEmpty()) {
            base = "ITEM";
        }
        base = base.substring(0, Math.min(base.length(), MAX_CODE_LENGTH - 4));

        String candidate = base;
        for (int suffix = 2; codeExists.test(candidate); suffix++) {
            candidate = base + "-" + suffix;
        }
        return candidate;
    }
}
