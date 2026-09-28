package com.hospital.manpower.service.interfaces;

import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;

import java.util.List;
import java.util.UUID;

public interface IReferenceDataService {

    List<LookupDTO> getOrganizations();

    List<LookupDTO> getLocations(UUID organizationId);

    List<LookupDTO> getHospitals(UUID locationId);

    List<LookupDTO> getDepartments(UUID hospitalId);

    List<LookupDTO> getDesignations(UUID organizationId);

    List<PlanningPeriodDTO> getPlanningPeriods();
}
