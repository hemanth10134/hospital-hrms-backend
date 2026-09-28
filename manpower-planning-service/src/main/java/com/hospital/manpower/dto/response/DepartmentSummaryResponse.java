package com.hospital.manpower.dto.response;

import java.math.BigDecimal;

public record DepartmentSummaryResponse(
        int totalRequiredStaff,
        int currentStaff,
        int openPositions,
        BigDecimal estimatedMonthlyBudget
) {
}
