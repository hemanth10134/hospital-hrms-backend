package com.hospital.manpower.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating a designation. organizationId comes from the URL
 * path, not the body, so it isn't duplicated/validated here.
 */
public record CreateDesignationBody(
        @NotBlank(message = "Designation code is required") String code,
        @NotBlank(message = "Designation name is required") String name
) {
}
