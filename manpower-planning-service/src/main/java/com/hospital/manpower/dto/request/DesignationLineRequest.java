package com.hospital.manpower.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DesignationLineRequest(
        @NotNull UUID designationId,
        @NotNull @DecimalMin(value = "0.01", message = "Staffing ratio must be greater than zero") BigDecimal staffingRatio,
        @NotNull @DecimalMin(value = "0", message = "Monthly salary cannot be negative") BigDecimal monthlySalary,
        @NotNull @DecimalMin(value = "0", message = "Leave buffer cannot be negative") BigDecimal leaveBufferPct,
        @NotNull @Min(value = 0, message = "Current staff cannot be negative") Integer currentStaff
) {
}
