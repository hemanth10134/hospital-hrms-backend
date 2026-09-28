package com.hospital.manpower.dto.request;

import com.hospital.manpower.enums.PositionRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePositionRequestStatusRequest(
        @NotNull PositionRequestStatus status,
        @NotBlank(message = "Reviewed by is required") String reviewedBy
) {
}
