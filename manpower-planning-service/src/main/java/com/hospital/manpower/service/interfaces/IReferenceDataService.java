package com.hospital.manpower.service.interfaces;

import com.hospital.manpower.dto.request.CreateDesignationRequest;
import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;
import com.hospital.manpower.dto.response.ReferenceHierarchyDTO;

import java.util.List;
import java.util.UUID;

public interface IReferenceDataService {

    List<LookupDTO> getOrganizations();

    List<LookupDTO> getLocations(UUID organizationId);

    List<LookupDTO> getHospitals(UUID locationId);

    List<LookupDTO> getDepartments(UUID hospitalId);

    List<LookupDTO> getDesignations(UUID organizationId);

    LookupDTO createDesignation(CreateDesignationRequest request);

    LookupDTO createLocation(UUID organizationId, String name);

    LookupDTO createHospital(UUID locationId, String name);

    LookupDTO createDepartment(UUID hospitalId, String name);

    List<ReferenceHierarchyDTO> getHierarchy();

    List<PlanningPeriodDTO> getPlanningPeriods();
}
