package com.hospital.manpower.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full replace-style update for a DRAFT plan: department parameters and the
 * complete designation-wise staffing list. Simpler and less error-prone than
 * patch-style partial updates for a form that is saved as a whole (KISS).
 */
public record UpdateManpowerPlanRequest(
        @NotNull @Min(value = 0, message = "Number of beds cannot be negative") Integer numberOfBeds,
        @NotNull @DecimalMin(value = "0.1", message = "Operating hours must be greater than zero") BigDecimal departmentOperatingHours,
        @NotNull @DecimalMin(value = "0.1", message = "Working hours must be greater than zero") BigDecimal employeeWorkingHours,
        @NotNull List<@Valid DesignationLineRequest> designations
) {
}
