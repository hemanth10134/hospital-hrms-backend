package com.hospital.manpower.controller;

import com.hospital.manpower.dto.request.CreateManpowerPlanRequest;
import com.hospital.manpower.dto.request.SubmitPlanRequest;
import com.hospital.manpower.dto.request.UpdateManpowerPlanRequest;
import com.hospital.manpower.dto.response.ManpowerPlanResponse;
import com.hospital.manpower.dto.response.ManpowerPlanSummaryListItem;
import com.hospital.manpower.service.interfaces.IManpowerPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manpower-plans")
@RequiredArgsConstructor
public class ManpowerPlanController {

    private final IManpowerPlanService manpowerPlanService;

    @PostMapping
    public ResponseEntity<ManpowerPlanResponse> createPlan(@Valid @RequestBody CreateManpowerPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manpowerPlanService.createPlan(request));
    }

    @GetMapping("/{planId}")
    public ManpowerPlanResponse getPlan(@PathVariable UUID planId) {
        return manpowerPlanService.getPlan(planId);
    }

    @GetMapping
    public List<ManpowerPlanSummaryListItem> getPreviousPlans(@RequestParam UUID hospitalId,
                                                                @RequestParam UUID departmentId) {
        return manpowerPlanService.getPreviousPlans(hospitalId, departmentId);
    }

    @PutMapping("/{planId}")
    public ManpowerPlanResponse updatePlan(@PathVariable UUID planId,
                                            @Valid @RequestBody UpdateManpowerPlanRequest request) {
        return manpowerPlanService.updatePlan(planId, request);
    }

    @PostMapping("/{planId}/submit")
    public ManpowerPlanResponse submitForApproval(@PathVariable UUID planId,
                                                   @Valid @RequestBody SubmitPlanRequest request) {
        return manpowerPlanService.submitForApproval(planId, request.submittedBy());
    }
}
