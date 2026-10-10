package com.hospital.manpower.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDesignationRequest(
        @NotNull UUID organizationId,
        @NotBlank(message = "Designation code is required") String code,
        @NotBlank(message = "Designation name is required") String name
) {
}
