package com.hospital.manpower.controller;

import com.hospital.manpower.dto.request.CreateDesignationBody;
import com.hospital.manpower.dto.request.CreateDesignationRequest;
import com.hospital.manpower.dto.request.CreateReferenceDataBody;
import com.hospital.manpower.dto.response.LookupDTO;
import com.hospital.manpower.dto.response.PlanningPeriodDTO;
import com.hospital.manpower.dto.response.ReferenceHierarchyDTO;
import com.hospital.manpower.service.interfaces.IReferenceDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/organizations/{organizationId}/locations")
    public ResponseEntity<LookupDTO> createLocation(@PathVariable UUID organizationId,
                                                    @Valid @RequestBody CreateReferenceDataBody body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(referenceDataService.createLocation(organizationId, body.name()));
    }

    @GetMapping("/locations/{locationId}/hospitals")
    public List<LookupDTO> getHospitals(@PathVariable UUID locationId) {
        return referenceDataService.getHospitals(locationId);
    }

    @PostMapping("/locations/{locationId}/hospitals")
    public ResponseEntity<LookupDTO> createHospital(@PathVariable UUID locationId,
                                                    @Valid @RequestBody CreateReferenceDataBody body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(referenceDataService.createHospital(locationId, body.name()));
    }

    @GetMapping("/hospitals/{hospitalId}/departments")
    public List<LookupDTO> getDepartments(@PathVariable UUID hospitalId) {
        return referenceDataService.getDepartments(hospitalId);
    }

    @PostMapping("/hospitals/{hospitalId}/departments")
    public ResponseEntity<LookupDTO> createDepartment(@PathVariable UUID hospitalId,
                                                      @Valid @RequestBody CreateReferenceDataBody body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(referenceDataService.createDepartment(hospitalId, body.name()));
    }

    @GetMapping("/reference-data/hierarchy")
    public List<ReferenceHierarchyDTO> getHierarchy() {
        return referenceDataService.getHierarchy();
    }

    @GetMapping("/organizations/{organizationId}/designations")
    public List<LookupDTO> getDesignations(@PathVariable UUID organizationId) {
        return referenceDataService.getDesignations(organizationId);
    }

    @PostMapping("/organizations/{organizationId}/designations")
    public ResponseEntity<LookupDTO> createDesignation(@PathVariable UUID organizationId,
                                                         @Valid @RequestBody CreateDesignationBody body) {
        LookupDTO created = referenceDataService.createDesignation(
                new CreateDesignationRequest(organizationId, body.code(), body.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/planning-periods")
    public List<PlanningPeriodDTO> getPlanningPeriods() {
        return referenceDataService.getPlanningPeriods();
    }
}
