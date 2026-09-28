package com.hospital.manpower.service.interfaces;

import com.hospital.manpower.dto.request.CreateManpowerPlanRequest;
import com.hospital.manpower.dto.request.UpdateManpowerPlanRequest;
import com.hospital.manpower.dto.response.ManpowerPlanResponse;
import com.hospital.manpower.dto.response.ManpowerPlanSummaryListItem;

import java.util.List;
import java.util.UUID;

public interface IManpowerPlanService {

    ManpowerPlanResponse createPlan(CreateManpowerPlanRequest request);

    ManpowerPlanResponse getPlan(UUID planId);

    List<ManpowerPlanSummaryListItem> getPreviousPlans(UUID hospitalId, UUID departmentId);

    ManpowerPlanResponse updatePlan(UUID planId, UpdateManpowerPlanRequest request);

    ManpowerPlanResponse submitForApproval(UUID planId, String submittedBy);
}
