package com.hospital.manpower.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePositionRequestRequest(
        UUID planId,
        @NotNull UUID departmentId,
        @NotNull UUID designationId,
        @NotNull @Min(value = 1, message = "Requested positions must be at least 1") Integer requestedPositions,
        @NotBlank(message = "Reason is required") String reason,
        @NotNull @DecimalMin(value = "0", message = "Additional monthly budget cannot be negative") BigDecimal additionalMonthlyBudget,
        @NotBlank(message = "Requested by is required") String requestedBy
) {
}
