package com.hospital.manpower.controller;

import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;
import com.hospital.manpower.service.interfaces.IReferenceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReferenceDataController {

    private final IReferenceDataService referenceDataService;

    @GetMapping("/organizations")
    public List<LookupDTO> getOrganizations() {
        return referenceDataService.getOrganizations();
    }

    @GetMapping("/organizations/{organizationId}/locations")
    public List<LookupDTO> getLocations(@PathVariable UUID organizationId) {
        return referenceDataService.getLocations(organizationId);
    }

    @GetMapping("/locations/{locationId}/hospitals")
    public List<LookupDTO> getHospitals(@PathVariable UUID locationId) {
        return referenceDataService.getHospitals(locationId);
    }

    @GetMapping("/hospitals/{hospitalId}/departments")
    public List<LookupDTO> getDepartments(@PathVariable UUID hospitalId) {
        return referenceDataService.getDepartments(hospitalId);
    }

    @GetMapping("/organizations/{organizationId}/designations")
    public List<LookupDTO> getDesignations(@PathVariable UUID organizationId) {
        return referenceDataService.getDesignations(organizationId);
    }

    @GetMapping("/planning-periods")
    public List<PlanningPeriodDTO> getPlanningPeriods() {
        return referenceDataService.getPlanningPeriods();
    }
}
