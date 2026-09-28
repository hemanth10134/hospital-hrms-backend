package com.hospital.manpower.service;

import com.hospital.manpower.dto.request.CreateManpowerPlanRequest;
import com.hospital.manpower.dto.request.DesignationLineRequest;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.exception.DuplicatePlanException;
import com.hospital.manpower.exception.InvalidPlanStateException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.impl.ManpowerPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManpowerPlanServiceImplTest {

    @Mock
    private ManpowerPlanRepository manpowerPlanRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DesignationRepository designationRepository;
    @Mock
    private PlanningPeriodRepository planningPeriodRepository;

    private ManpowerPlanServiceImpl service;

    private UUID organizationId;
    private UUID locationId;
    private UUID hospitalId;
    private UUID departmentId;
    private UUID planningPeriodId;
    private UUID designationId;

    @BeforeEach
    void setUp() {
        service = new ManpowerPlanServiceImpl(
                manpowerPlanRepository, organizationRepository, locationRepository, hospitalRepository,
                departmentRepository, designationRepository, planningPeriodRepository, new ManpowerPlanMapper());

        organizationId = UUID.randomUUID();
        locationId = UUID.randomUUID();
        hospitalId = UUID.randomUUID();
        departmentId = UUID.randomUUID();
        planningPeriodId = UUID.randomUUID();
        designationId = UUID.randomUUID();
    }

    private CreateManpowerPlanRequest buildRequest(List<DesignationLineRequest> lines) {
        return new CreateManpowerPlanRequest(
                organizationId, locationId, hospitalId, departmentId, planningPeriodId,
                20, BigDecimal.valueOf(24), BigDecimal.valueOf(8), "sunita.sharma", lines);
    }

    private void stubReferenceLookups() {
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setCode("PARK-GROUP");
        organization.setName("Park Group");

        Location location = new Location();
        location.setId(locationId);
        location.setCode("DEL");
        location.setName("Delhi");

        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        hospital.setCode("PARK-MAIN");
        hospital.setName("Park Hospital (Main)");

        Department department = new Department();
        department.setId(departmentId);
        department.setCode("ICU");
        department.setName("ICU - Intensive Care Unit");

        PlanningPeriod period = new PlanningPeriod();
        period.setId(planningPeriodId);
        period.setCode("FY2026-27");
        period.setLabel("Apr 2026 - Mar 2027");

        Designation designation = new Designation();
        designation.setId(designationId);
        designation.setCode("CONS-INTENSIVIST");
        designation.setName("Consultant Intensivist");

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(planningPeriodRepository.findById(planningPeriodId)).thenReturn(Optional.of(period));
        when(designationRepository.findById(designationId)).thenReturn(Optional.of(designation));
    }

    @Test
    void createPlan_computesRequiredStaffAndBudgetForEachDesignationLine() {
        stubReferenceLookups();
        when(manpowerPlanRepository.findByHospitalIdAndDepartmentIdAndPlanningPeriodId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(manpowerPlanRepository.save(any(ManpowerPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DesignationLineRequest line = new DesignationLineRequest(
                designationId, BigDecimal.valueOf(10), BigDecimal.valueOf(150000), BigDecimal.valueOf(10), 2);

        var response = service.createPlan(buildRequest(List.of(line)));

        assertThat(response.designations()).hasSize(1);
        var designationLine = response.designations().get(0);
        assertThat(designationLine.requiredStaff()).isEqualTo(7);
        assertThat(designationLine.vacancies()).isEqualTo(5);
        assertThat(designationLine.monthlyBudget()).isEqualByComparingTo(BigDecimal.valueOf(1_050_000));
        assertThat(response.summary().totalRequiredStaff()).isEqualTo(7);
        assertThat(response.status()).isEqualTo("DRAFT");
    }

    @Test
    void createPlan_rejectsDuplicatePlanForSameHospitalDepartmentAndPeriod() {
        when(manpowerPlanRepository.findByHospitalIdAndDepartmentIdAndPlanningPeriodId(any(), any(), any()))
                .thenReturn(Optional.of(new ManpowerPlan()));

        assertThatThrownBy(() -> service.createPlan(buildRequest(List.of())))
                .isInstanceOf(DuplicatePlanException.class);

        verify(manpowerPlanRepository, never()).save(any());
    }

    @Test
    void submitForApproval_rejectsPlanWithNoDesignationLines() {
        ManpowerPlan plan = new ManpowerPlan();
        plan.setId(UUID.randomUUID());
        when(manpowerPlanRepository.findById(plan.getId())).thenReturn(Optional.of(plan));

        assertThatThrownBy(() -> service.submitForApproval(plan.getId(), "sunita.sharma"))
                .isInstanceOf(InvalidPlanStateException.class);

        verify(manpowerPlanRepository, never()).save(any());
    }
}
