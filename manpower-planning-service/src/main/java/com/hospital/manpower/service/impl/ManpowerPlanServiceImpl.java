package com.hospital.manpower.service.impl;

import com.hospital.manpower.constants.ErrorMessages;
import com.hospital.manpower.dto.request.CreateManpowerPlanRequest;
import com.hospital.manpower.dto.request.DesignationLineRequest;
import com.hospital.manpower.dto.request.UpdateManpowerPlanRequest;
import com.hospital.manpower.dto.response.ManpowerPlanResponse;
import com.hospital.manpower.dto.response.ManpowerPlanSummaryListItem;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.enums.PlanStatus;
import com.hospital.manpower.exception.DuplicatePlanException;
import com.hospital.manpower.exception.InvalidPlanStateException;
import com.hospital.manpower.exception.ResourceNotFoundException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.interfaces.IManpowerPlanService;
import com.hospital.manpower.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ManpowerPlanServiceImpl implements IManpowerPlanService {

    private static final Logger log = LoggerFactory.getLogger(ManpowerPlanServiceImpl.class);

    private final ManpowerPlanRepository manpowerPlanRepository;
    private final OrganizationRepository organizationRepository;
    private final LocationRepository locationRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PlanningPeriodRepository planningPeriodRepository;
    private final ManpowerPlanMapper mapper;

    @Override
    public ManpowerPlanResponse createPlan(CreateManpowerPlanRequest request) {
        manpowerPlanRepository.findByHospitalIdAndDepartmentIdAndPlanningPeriodId(
                        request.hospitalId(), request.departmentId(), request.planningPeriodId())
                .ifPresent(existing -> {
                    throw new DuplicatePlanException(ErrorMessages.PLAN_ALREADY_EXISTS);
                });

        ManpowerPlan plan = new ManpowerPlan();
        plan.setOrganization(getOrganization(request.organizationId()));
        plan.setLocation(getLocation(request.locationId()));
        plan.setHospital(getHospital(request.hospitalId()));
        plan.setDepartment(getDepartment(request.departmentId()));
        plan.setPlanningPeriod(getPlanningPeriod(request.planningPeriodId()));
        plan.setNumberOfBeds(request.numberOfBeds());
        plan.setDepartmentOperatingHours(request.departmentOperatingHours());
        plan.setEmployeeWorkingHours(request.employeeWorkingHours());
        plan.setCreatedBy(request.createdBy());
        plan.setStatus(PlanStatus.DRAFT);

        applyDesignationLines(plan, request.designations());

        ManpowerPlan saved = manpowerPlanRepository.save(plan);
        log.info("Created manpower plan {} for hospital {} department {}", saved.getId(),
                request.hospitalId(), request.departmentId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ManpowerPlanResponse getPlan(UUID planId) {
        return mapper.toResponse(getPlanOrThrow(planId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManpowerPlanSummaryListItem> getPreviousPlans(UUID hospitalId, UUID departmentId) {
        return manpowerPlanRepository.findByHospitalIdAndDepartmentIdOrderByCreatedAtDesc(hospitalId, departmentId)
                .stream()
                .map(mapper::toListItem)
                .toList();
    }

    @Override
    public ManpowerPlanResponse updatePlan(UUID planId, UpdateManpowerPlanRequest request) {
        ManpowerPlan plan = getPlanOrThrow(planId);
        if (plan.getStatus() != PlanStatus.DRAFT) {
            throw new InvalidPlanStateException(ErrorMessages.ONLY_DRAFT_CAN_BE_MODIFIED);
        }

        plan.setNumberOfBeds(request.numberOfBeds());
        plan.setDepartmentOperatingHours(request.departmentOperatingHours());
        plan.setEmployeeWorkingHours(request.employeeWorkingHours());

        plan.getDesignations().clear();
        applyDesignationLines(plan, request.designations());

        ManpowerPlan saved = manpowerPlanRepository.save(plan);
        log.info("Updated draft manpower plan {}", planId);
        return mapper.toResponse(saved);
    }

    @Override
    public ManpowerPlanResponse submitForApproval(UUID planId, String submittedBy) {
        ManpowerPlan plan = getPlanOrThrow(planId);
        if (plan.getStatus() != PlanStatus.DRAFT) {
            throw new InvalidPlanStateException(ErrorMessages.ONLY_DRAFT_CAN_BE_SUBMITTED);
        }
        if (plan.getDesignations().isEmpty()) {
            throw new InvalidPlanStateException(ErrorMessages.PLAN_MUST_HAVE_DESIGNATION);
        }

        plan.setStatus(PlanStatus.SUBMITTED);
        plan.setSubmittedBy(submittedBy);
        plan.setSubmittedAt(DateTimeUtil.now());

        ManpowerPlan saved = manpowerPlanRepository.save(plan);
        log.info("Submitted manpower plan {} for approval by {}", planId, submittedBy);
        return mapper.toResponse(saved);
    }

    private void applyDesignationLines(ManpowerPlan plan, List<DesignationLineRequest> lineRequests) {
        int order = 0;
        for (DesignationLineRequest lineRequest : lineRequests) {
            ManpowerPlanDesignation line = new ManpowerPlanDesignation();
            line.setPlan(plan);
            line.setDesignation(getDesignation(lineRequest.designationId()));
            line.setStaffingRatio(lineRequest.staffingRatio());
            line.setMonthlySalary(lineRequest.monthlySalary());
            line.setLeaveBufferPct(lineRequest.leaveBufferPct());
            line.setCurrentStaff(lineRequest.currentStaff());
            line.setDisplayOrder(order++);
            plan.getDesignations().add(line);
        }
    }

    private ManpowerPlan getPlanOrThrow(UUID planId) {
        return manpowerPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MANPOWER_PLAN_NOT_FOUND, planId)));
    }

    private Organization getOrganization(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORGANIZATION_NOT_FOUND, id)));
    }

    private Location getLocation(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.LOCATION_NOT_FOUND, id)));
    }

    private Hospital getHospital(UUID id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.HOSPITAL_NOT_FOUND, id)));
    }

    private Department getDepartment(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, id)));
    }

    private Designation getDesignation(UUID id) {
        return designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.DESIGNATION_NOT_FOUND, id)));
    }

    private PlanningPeriod getPlanningPeriod(UUID id) {
        return planningPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.PLANNING_PERIOD_NOT_FOUND, id)));
    }
}
