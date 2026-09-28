package com.hospital.manpower.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateManpowerPlanRequest(
        @NotNull UUID organizationId,
        @NotNull UUID locationId,
        @NotNull UUID hospitalId,
        @NotNull UUID departmentId,
        @NotNull UUID planningPeriodId,
        @NotNull @Min(value = 0, message = "Number of beds cannot be negative") Integer numberOfBeds,
        @NotNull @DecimalMin(value = "0.1", message = "Operating hours must be greater than zero") BigDecimal departmentOperatingHours,
        @NotNull @DecimalMin(value = "0.1", message = "Working hours must be greater than zero") BigDecimal employeeWorkingHours,
        @NotNull String createdBy,
        @NotNull List<@Valid DesignationLineRequest> designations
) {
}
