package com.hospital.manpower.service;

import com.hospital.manpower.dto.request.ManpowerPlanReportFilter;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.enums.PlanStatus;
import com.hospital.manpower.enums.PositionRequestStatus;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.ManpowerPlanRepository;
import com.hospital.manpower.repository.PositionRequestRepository;
import com.hospital.manpower.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ManpowerPlanRepository manpowerPlanRepository;
    @Mock
    private PositionRequestRepository positionRequestRepository;

    @Test
    void generateReport_attachesOpenPositionRequestsAndStaffingStatusToEachLine() {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("ICU");
        Designation designation = new Designation();
        designation.setId(UUID.randomUUID());
        designation.setName("Staff Nurse");
        Organization organization = new Organization();
        organization.setName("Park Group");
        Location location = new Location();
        location.setName("Delhi");
        Hospital hospital = new Hospital();
        hospital.setName("Park Hospital");

        ManpowerPlan plan = new ManpowerPlan();
        plan.setId(UUID.randomUUID());
        plan.setOrganization(organization);
        plan.setLocation(location);
        plan.setHospital(hospital);
        plan.setDepartment(department);
        plan.setNumberOfBeds(20);
        plan.setDepartmentOperatingHours(BigDecimal.valueOf(24));
        plan.setEmployeeWorkingHours(BigDecimal.valueOf(8));
        plan.setStatus(PlanStatus.DRAFT);

        ManpowerPlanDesignation line = new ManpowerPlanDesignation();
        line.setPlan(plan);
        line.setDesignation(designation);
        line.setStaffingRatio(BigDecimal.TEN);
        line.setMonthlySalary(BigDecimal.valueOf(50000));
        line.setLeaveBufferPct(BigDecimal.ZERO);
        line.setCurrentStaff(2);
        plan.getDesignations().add(line);

        PositionRequest request = new PositionRequest();
        request.setDepartment(department);
        request.setDesignation(designation);
        request.setRequestedPositions(3);
        request.setAdditionalMonthlyBudget(BigDecimal.valueOf(150000));
        request.setReason("Surge");
        request.setRequestedBy("Sunita");
        request.setStatus(PositionRequestStatus.PENDING_APPROVAL);

        when(manpowerPlanRepository.findAll()).thenReturn(List.of(plan));
        when(positionRequestRepository.findByStatusNot(PositionRequestStatus.REJECTED)).thenReturn(List.of(request));

        ReportServiceImpl service = new ReportServiceImpl(manpowerPlanRepository, positionRequestRepository, new ManpowerPlanMapper());
        var rows = service.generateReport(new ManpowerPlanReportFilter(null, null, null, null, null, null, null, null));

        assertThat(rows).hasSize(1);
        var row = rows.get(0);
        assertThat(row.requiredStaff()).isEqualTo(6);
        assertThat(row.staffingStatus()).isEqualTo("Vacant");
        assertThat(row.requestedPositions()).isEqualTo(3);
        assertThat(row.additionalMonthlyBudget()).isEqualByComparingTo(BigDecimal.valueOf(150000));
        assertThat(row.requestReasons()).isEqualTo("Surge");
        assertThat(row.requestedBy()).isEqualTo("Sunita");
    }
}
