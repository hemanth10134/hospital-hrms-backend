package com.hospital.manpower.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PositionRequestResponse(
        UUID id,
        LookupDTO department,
        LookupDTO designation,
        Integer requestedPositions,
        String reason,
        BigDecimal additionalMonthlyBudget,
        String status,
        String requestedBy,
        String reviewedBy,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt
) {
}
