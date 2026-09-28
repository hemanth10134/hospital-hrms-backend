package com.hospital.manpower.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight row used for "View Previous Plan" listing, without the full designation list.
 */
public record ManpowerPlanSummaryListItem(
        UUID id,
        String planningPeriodLabel,
        String status,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime submittedAt
) {
}
