package com.hospital.manpower.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.manpower.dto.request.CreateManpowerPlanRequest;
import com.hospital.manpower.dto.request.DesignationLineRequest;
import com.hospital.manpower.dto.request.SubmitPlanRequest;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ManpowerPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DesignationRepository designationRepository;
    @Autowired
    private PlanningPeriodRepository planningPeriodRepository;

    private Organization organization;
    private Location location;
    private Hospital hospital;
    private Department department;
    private Designation designation;
    private PlanningPeriod planningPeriod;

    @BeforeEach
    void seedReferenceData() {
        organization = organizationRepository.save(newOrganization());
        location = locationRepository.save(newLocation(organization));
        hospital = hospitalRepository.save(newHospital(location));
        department = departmentRepository.save(newDepartment(hospital));
        designation = designationRepository.save(newDesignation(organization));
        planningPeriod = planningPeriodRepository.save(newPlanningPeriod());
    }

    @Test
    void createGetAndSubmitPlan_endToEnd() throws Exception {
        DesignationLineRequest line = new DesignationLineRequest(
                designation.getId(), BigDecimal.valueOf(10), BigDecimal.valueOf(150000), BigDecimal.valueOf(10), 2);

        CreateManpowerPlanRequest createRequest = new CreateManpowerPlanRequest(
                organization.getId(), location.getId(), hospital.getId(), department.getId(), planningPeriod.getId(),
                20, BigDecimal.valueOf(24), BigDecimal.valueOf(8), "sunita.sharma", List.of(line));

        String createResponseJson = mockMvc.perform(post("/api/v1/manpower-plans")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.designations[0].requiredStaff", is(7)))
                .andReturn().getResponse().getContentAsString();

        String planId = objectMapper.readTree(createResponseJson).get("id").asText();

        mockMvc.perform(get("/api/v1/manpower-plans/{id}", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalRequiredStaff", is(7)));

        SubmitPlanRequest submitRequest = new SubmitPlanRequest("sunita.sharma");
        mockMvc.perform(post("/api/v1/manpower-plans/{id}/submit", planId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUBMITTED")));
    }

    private Organization newOrganization() {
        Organization organization = new Organization();
        organization.setCode("PARK-GROUP");
        organization.setName("Park Group");
        return organization;
    }

    private Location newLocation(Organization organization) {
        Location location = new Location();
        location.setOrganization(organization);
        location.setCode("DEL");
        location.setName("Delhi");
        return location;
    }

    private Hospital newHospital(Location location) {
        Hospital hospital = new Hospital();
        hospital.setLocation(location);
        hospital.setCode("PARK-MAIN");
        hospital.setName("Park Hospital (Main)");
        return hospital;
    }

    private Department newDepartment(Hospital hospital) {
        Department department = new Department();
        department.setHospital(hospital);
        department.setCode("ICU");
        department.setName("ICU - Intensive Care Unit");
        return department;
    }

    private Designation newDesignation(Organization organization) {
        Designation designation = new Designation();
        designation.setOrganization(organization);
        designation.setCode("CONS-INTENSIVIST");
        designation.setName("Consultant Intensivist");
        return designation;
    }

    private PlanningPeriod newPlanningPeriod() {
        PlanningPeriod period = new PlanningPeriod();
        period.setCode("FY2026-27");
        period.setLabel("Apr 2026 - Mar 2027");
        period.setStartDate(LocalDate.of(2026, 4, 1));
        period.setEndDate(LocalDate.of(2027, 3, 31));
        return period;
    }
}
