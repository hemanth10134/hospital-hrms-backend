package com.hospital.manpower.service.interfaces;

import com.hospital.manpower.dto.request.ManpowerPlanReportFilter;
import com.hospital.manpower.dto.response.ManpowerPlanReportRow;

import java.util.List;

public interface IReportService {

    List<ManpowerPlanReportRow> generateReport(ManpowerPlanReportFilter filter);

    String exportReportCsv(ManpowerPlanReportFilter filter);
}
