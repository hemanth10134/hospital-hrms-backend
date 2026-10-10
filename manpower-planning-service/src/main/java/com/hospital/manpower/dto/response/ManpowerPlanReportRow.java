package com.hospital.manpower.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One designation-wise staffing line flattened for the Analysis/Reports screen
 * and CSV export, carrying the full organization → designation hierarchy so a
 * report row never needs a follow-up lookup.
 */
public record ManpowerPlanReportRow(
        UUID planId,
        String organizationName,
        String locationName,
        String hospitalName,
        String departmentName,
        String designationName,
        Integer numberOfBeds,
        BigDecimal departmentOperatingHours,
        BigDecimal employeeWorkingHours,
        BigDecimal staffingRatio,
        BigDecimal monthlySalary,
        BigDecimal leaveBufferPct,
        Boolean planned,
        Integer requiredStaff,
        Integer currentStaff,
        Integer vacancies,
        Integer excess,
        BigDecimal monthlyBudget,
        String planStatus
) {
}
