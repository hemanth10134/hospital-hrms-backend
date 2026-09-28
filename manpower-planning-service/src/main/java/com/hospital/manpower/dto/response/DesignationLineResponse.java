package com.hospital.manpower.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A designation-wise staffing row with derived fields (requiredStaff, vacancies,
 * excess, monthlyBudget) already computed by the service layer.
 */
public record DesignationLineResponse(
        UUID id,
        UUID designationId,
        String designationName,
        BigDecimal staffingRatio,
        BigDecimal monthlySalary,
        BigDecimal leaveBufferPct,
        Integer requiredStaff,
        Integer currentStaff,
        Integer vacancies,
        Integer excess,
        BigDecimal monthlyBudget
) {
}
