package com.hospital.manpower.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ManpowerPlanResponse(
        UUID id,
        LookupDTO organization,
        LookupDTO location,
        LookupDTO hospital,
        LookupDTO department,
        PlanningPeriodDTO planningPeriod,
        Integer numberOfBeds,
        BigDecimal departmentOperatingHours,
        BigDecimal employeeWorkingHours,
        String status,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<DesignationLineResponse> designations,
        DepartmentSummaryResponse summary
) {
}
