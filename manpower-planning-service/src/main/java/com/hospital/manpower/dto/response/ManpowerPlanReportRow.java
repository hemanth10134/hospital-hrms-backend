package com.hospital.manpower.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One designation-wise staffing line flattened for the Analysis/Reports screen
 * and export, carrying the full organization → designation hierarchy plus the
 * open (non-rejected) additional position requests raised for the same
 * department + designation, so a report row never needs a follow-up lookup.
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
        String staffingStatus,
        BigDecimal monthlyBudget,
        String planStatus,
        Integer requestedPositions,
        BigDecimal additionalMonthlyBudget,
        String requestReasons,
        String requestedBy
) {
}
