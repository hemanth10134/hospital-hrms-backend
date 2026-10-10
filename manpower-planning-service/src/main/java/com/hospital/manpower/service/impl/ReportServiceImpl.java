package com.hospital.manpower.service.impl;

import com.hospital.manpower.dto.request.ManpowerPlanReportFilter;
import com.hospital.manpower.dto.response.ManpowerPlanReportRow;
import com.hospital.manpower.entity.ManpowerPlan;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.ManpowerPlanRepository;
import com.hospital.manpower.service.interfaces.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Builds the Analysis/Reports dataset by flattening every manpower plan's
 * designation-wise staffing lines and filtering across the full
 * organization → designation hierarchy, plus status/vacancy/planned state.
 * Filtering happens in memory: this module's data volume (one row per
 * hospital/department/designation/period) stays small enough that a simple
 * stream pipeline is clearer than hand-written JPQL for every filter
 * combination (KISS).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements IReportService {

    private static final String CSV_HEADER = String.join(",",
            "Organization", "Location", "Hospital", "Department", "Designation",
            "No of Beds", "Operating Hours", "Employee Working Hours",
            "Staffing Ratio", "Monthly Salary", "Leave Buffer (%)", "Planned",
            "Required Staff (Position)", "Current Staff", "Vacancies", "Excess",
            "Monthly Budget", "Status");

    private final ManpowerPlanRepository manpowerPlanRepository;
    private final ManpowerPlanMapper mapper;

    @Override
    public List<ManpowerPlanReportRow> generateReport(ManpowerPlanReportFilter filter) {
        return manpowerPlanRepository.findAll().stream()
                .filter(plan -> matchesPlan(plan, filter))
                .flatMap(plan -> plan.getDesignations().stream()
                        .filter(line -> filter.designationId() == null
                                || filter.designationId().equals(line.getDesignation().getId()))
                        .map(line -> mapper.toReportRow(line, plan)))
                .filter(row -> filter.matchesVacancy(row.vacancies()))
                .filter(row -> filter.matchesPlanned(Boolean.TRUE.equals(row.planned())))
                .toList();
    }

    @Override
    public String exportReportCsv(ManpowerPlanReportFilter filter) {
        StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
        for (ManpowerPlanReportRow row : generateReport(filter)) {
            csv.append(String.join(",",
                            quote(row.organizationName()), quote(row.locationName()), quote(row.hospitalName()),
                            quote(row.departmentName()), quote(row.designationName()),
                            String.valueOf(row.numberOfBeds()), String.valueOf(row.departmentOperatingHours()),
                            String.valueOf(row.employeeWorkingHours()), String.valueOf(row.staffingRatio()),
                            String.valueOf(row.monthlySalary()), String.valueOf(row.leaveBufferPct()),
                            row.planned() ? "Planned" : "Not Planned", String.valueOf(row.requiredStaff()),
                            String.valueOf(row.currentStaff()), String.valueOf(row.vacancies()),
                            String.valueOf(row.excess()), String.valueOf(row.monthlyBudget()), row.planStatus()))
                    .append('\n');
        }
        return csv.toString();
    }

    private boolean matchesPlan(ManpowerPlan plan, ManpowerPlanReportFilter filter) {
        if (filter.organizationId() != null && !filter.organizationId().equals(plan.getOrganization().getId())) {
            return false;
        }
        if (filter.locationId() != null && !filter.locationId().equals(plan.getLocation().getId())) {
            return false;
        }
        if (filter.hospitalId() != null && !filter.hospitalId().equals(plan.getHospital().getId())) {
            return false;
        }
        if (filter.departmentId() != null && !filter.departmentId().equals(plan.getDepartment().getId())) {
            return false;
        }
        return filter.planStatus() == null || filter.planStatus().equalsIgnoreCase(plan.getStatus().name());
    }

    private String quote(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }
}
