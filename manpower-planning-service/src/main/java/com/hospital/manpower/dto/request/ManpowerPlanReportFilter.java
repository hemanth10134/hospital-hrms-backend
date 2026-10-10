package com.hospital.manpower.dto.request;

import java.util.UUID;

/**
 * Optional filters for the Analysis/Reports screen. Every field is nullable;
 * an absent field means "don't filter on this dimension".
 */
public record ManpowerPlanReportFilter(
        UUID organizationId,
        UUID locationId,
        UUID hospitalId,
        UUID departmentId,
        UUID designationId,
        String planStatus,
        String vacancyFilter,
        String plannedFilter
) {
    public boolean matchesVacancy(int vacancies) {
        if ("VACANT_ONLY".equalsIgnoreCase(vacancyFilter)) {
            return vacancies > 0;
        }
        if ("FULLY_STAFFED".equalsIgnoreCase(vacancyFilter)) {
            return vacancies == 0;
        }
        return true;
    }

    public boolean matchesPlanned(boolean planned) {
        if ("PLANNED".equalsIgnoreCase(plannedFilter)) {
            return planned;
        }
        if ("NOT_PLANNED".equalsIgnoreCase(plannedFilter)) {
            return !planned;
        }
        return true;
    }
}
