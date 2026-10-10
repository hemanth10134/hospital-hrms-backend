package com.hospital.manpower.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for adding a location, hospital or department. The parent id
 * comes from the URL path; the code is derived from the name server-side.
 */
public record CreateReferenceDataBody(
        @NotBlank(message = "Name is required") @Size(max = 150, message = "Name must be at most 150 characters") String name
) {
}
