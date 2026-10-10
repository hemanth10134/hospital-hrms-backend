package com.hospital.manpower.mapper;

import com.hospital.manpower.dto.response.*;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.util.StaffingCalculationUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ManpowerPlanMapper {

    public LookupDTO toLookup(Organization organization) {
        return new LookupDTO(organization.getId(), organization.getCode(), organization.getName());
    }

    public LookupDTO toLookup(Location location) {
        return new LookupDTO(location.getId(), location.getCode(), location.getName());
    }

    public LookupDTO toLookup(Hospital hospital) {
        return new LookupDTO(hospital.getId(), hospital.getCode(), hospital.getName());
    }

    public LookupDTO toLookup(Department department) {
        return new LookupDTO(department.getId(), department.getCode(), department.getName());
    }

    public LookupDTO toLookup(Designation designation) {
        return new LookupDTO(designation.getId(), designation.getCode(), designation.getName());
    }

    public PlanningPeriodDTO toDto(PlanningPeriod period) {
        return new PlanningPeriodDTO(period.getId(), period.getCode(), period.getLabel(), period.getStartDate(), period.getEndDate());
    }

    public DesignationLineResponse toDesignationLineResponse(ManpowerPlanDesignation line, ManpowerPlan plan) {
        int requiredStaff = StaffingCalculationUtil.calculateRequiredStaff(
                plan.getNumberOfBeds(),
                line.getStaffingRatio(),
                plan.getDepartmentOperatingHours(),
                plan.getEmployeeWorkingHours(),
                line.getLeaveBufferPct());
        int currentStaff = line.getCurrentStaff();
        BigDecimal staffingPercentage = requiredStaff == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(currentStaff)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(requiredStaff), 2, java.math.RoundingMode.HALF_UP);

        return new DesignationLineResponse(
                line.getId(),
                line.getDesignation().getId(),
                line.getDesignation().getName(),
                line.getStaffingRatio(),
                line.getMonthlySalary(),
                line.getLeaveBufferPct(),
                line.getPlanned(),
                requiredStaff,
                currentStaff,
                staffingPercentage,
                StaffingCalculationUtil.calculateVacancies(requiredStaff, currentStaff),
                StaffingCalculationUtil.calculateExcess(requiredStaff, currentStaff),
                StaffingCalculationUtil.calculateMonthlyBudget(requiredStaff, line.getMonthlySalary()));
    }

    public DepartmentSummaryResponse toSummary(List<DesignationLineResponse> lines, List<PositionRequest> positionRequests) {
        int totalRequired = lines.stream().mapToInt(DesignationLineResponse::requiredStaff).sum();
        int totalCurrent = lines.stream().mapToInt(DesignationLineResponse::currentStaff).sum();
        BigDecimal totalBudget = lines.stream()
                .map(DesignationLineResponse::monthlyBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int additionalPositions = positionRequests.stream().mapToInt(PositionRequest::getRequestedPositions).sum();
        BigDecimal additionalBudget = positionRequests.stream()
                .map(PositionRequest::getAdditionalMonthlyBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DepartmentSummaryResponse(totalRequired, totalCurrent, totalRequired - totalCurrent, totalBudget,
                additionalPositions, additionalBudget);
    }

    public ManpowerPlanResponse toResponse(ManpowerPlan plan, List<PositionRequest> positionRequests) {
        List<DesignationLineResponse> lines = plan.getDesignations().stream()
                .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                .map(line -> toDesignationLineResponse(line, plan))
                .toList();

        return new ManpowerPlanResponse(
                plan.getId(),
                toLookup(plan.getOrganization()),
                toLookup(plan.getLocation()),
                toLookup(plan.getHospital()),
                toLookup(plan.getDepartment()),
                toDto(plan.getPlanningPeriod()),
                plan.getNumberOfBeds(),
                plan.getDepartmentOperatingHours(),
                plan.getEmployeeWorkingHours(),
                plan.getStatus().name(),
                plan.getCreatedBy(),
                plan.getCreatedAt(),
                plan.getUpdatedAt(),
                lines,
                toSummary(lines, positionRequests));
    }

    public ManpowerPlanSummaryListItem toListItem(ManpowerPlan plan) {
        return new ManpowerPlanSummaryListItem(
                plan.getId(),
                plan.getPlanningPeriod().getLabel(),
                plan.getStatus().name(),
                plan.getCreatedBy(),
                plan.getCreatedAt(),
                plan.getSubmittedAt());
    }

    public ManpowerPlanReportRow toReportRow(ManpowerPlanDesignation line, ManpowerPlan plan) {
        int requiredStaff = StaffingCalculationUtil.calculateRequiredStaff(
                plan.getNumberOfBeds(),
                line.getStaffingRatio(),
                plan.getDepartmentOperatingHours(),
                plan.getEmployeeWorkingHours(),
                line.getLeaveBufferPct());
        int currentStaff = line.getCurrentStaff();

        return new ManpowerPlanReportRow(
                plan.getId(),
                plan.getOrganization().getName(),
                plan.getLocation().getName(),
                plan.getHospital().getName(),
                plan.getDepartment().getName(),
                line.getDesignation().getName(),
                plan.getNumberOfBeds(),
                plan.getDepartmentOperatingHours(),
                plan.getEmployeeWorkingHours(),
                line.getStaffingRatio(),
                line.getMonthlySalary(),
                line.getLeaveBufferPct(),
                line.getPlanned(),
                requiredStaff,
                currentStaff,
                StaffingCalculationUtil.calculateVacancies(requiredStaff, currentStaff),
                StaffingCalculationUtil.calculateExcess(requiredStaff, currentStaff),
                StaffingCalculationUtil.calculateMonthlyBudget(requiredStaff, line.getMonthlySalary()),
                plan.getStatus().name());
    }

    public PositionRequestResponse toResponse(PositionRequest request) {
        return new PositionRequestResponse(
                request.getId(),
                toLookup(request.getDepartment()),
                toLookup(request.getDesignation()),
                request.getRequestedPositions(),
                request.getCurrentStaff(),
                request.getReason(),
                request.getAdditionalMonthlyBudget(),
                request.getStatus().name(),
                request.getRequestedBy(),
                request.getReviewedBy(),
                request.getReviewedAt(),
                request.getCreatedAt());
    }
}
