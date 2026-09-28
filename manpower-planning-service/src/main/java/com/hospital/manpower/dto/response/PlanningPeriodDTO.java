package com.hospital.manpower.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record PlanningPeriodDTO(UUID id, String code, String label, LocalDate startDate, LocalDate endDate) {
}
