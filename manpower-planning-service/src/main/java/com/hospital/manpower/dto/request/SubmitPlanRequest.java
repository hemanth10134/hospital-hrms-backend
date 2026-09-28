package com.hospital.manpower.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubmitPlanRequest(@NotNull String submittedBy) {
}
