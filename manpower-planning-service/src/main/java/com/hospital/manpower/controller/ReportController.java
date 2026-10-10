package com.hospital.manpower.controller;

import com.hospital.manpower.dto.request.ManpowerPlanReportFilter;
import com.hospital.manpower.dto.response.ManpowerPlanReportRow;
import com.hospital.manpower.service.interfaces.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports/manpower-plans")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    @GetMapping
    public List<ManpowerPlanReportRow> generateReport(
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID designationId,
            @RequestParam(required = false) String planStatus,
            @RequestParam(required = false) String vacancyFilter,
            @RequestParam(required = false) String plannedFilter) {
        return reportService.generateReport(new ManpowerPlanReportFilter(
                organizationId, locationId, hospitalId, departmentId, designationId,
                planStatus, vacancyFilter, plannedFilter));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID designationId,
            @RequestParam(required = false) String planStatus,
            @RequestParam(required = false) String vacancyFilter,
            @RequestParam(required = false) String plannedFilter) {
        String csv = reportService.exportReportCsv(new ManpowerPlanReportFilter(
                organizationId, locationId, hospitalId, departmentId, designationId,
                planStatus, vacancyFilter, plannedFilter));

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename("manpower-planning-report.csv")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
