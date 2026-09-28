package com.hospital.manpower.service.impl;

import com.hospital.manpower.constants.ErrorMessages;
import com.hospital.manpower.dto.request.CreatePositionRequestRequest;
import com.hospital.manpower.dto.request.UpdatePositionRequestStatusRequest;
import com.hospital.manpower.dto.response.PositionRequestResponse;
import com.hospital.manpower.entity.Department;
import com.hospital.manpower.entity.Designation;
import com.hospital.manpower.entity.ManpowerPlan;
import com.hospital.manpower.entity.PositionRequest;
import com.hospital.manpower.enums.PositionRequestStatus;
import com.hospital.manpower.exception.InvalidPlanStateException;
import com.hospital.manpower.exception.ResourceNotFoundException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.DepartmentRepository;
import com.hospital.manpower.repository.DesignationRepository;
import com.hospital.manpower.repository.ManpowerPlanRepository;
import com.hospital.manpower.repository.PositionRequestRepository;
import com.hospital.manpower.service.interfaces.IPositionRequestService;
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
public class PositionRequestServiceImpl implements IPositionRequestService {

    private static final Logger log = LoggerFactory.getLogger(PositionRequestServiceImpl.class);

    private final PositionRequestRepository positionRequestRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final ManpowerPlanRepository manpowerPlanRepository;
    private final ManpowerPlanMapper mapper;

    @Override
    public PositionRequestResponse createPositionRequest(CreatePositionRequestRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.DEPARTMENT_NOT_FOUND, request.departmentId())));
        Designation designation = designationRepository.findById(request.designationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.DESIGNATION_NOT_FOUND, request.designationId())));

        PositionRequest positionRequest = new PositionRequest();
        positionRequest.setDepartment(department);
        positionRequest.setDesignation(designation);
        positionRequest.setRequestedPositions(request.requestedPositions());
        positionRequest.setReason(request.reason());
        positionRequest.setAdditionalMonthlyBudget(request.additionalMonthlyBudget());
        positionRequest.setRequestedBy(request.requestedBy());
        positionRequest.setStatus(PositionRequestStatus.PENDING_APPROVAL);

        if (request.planId() != null) {
            ManpowerPlan plan = manpowerPlanRepository.findById(request.planId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format(ErrorMessages.MANPOWER_PLAN_NOT_FOUND, request.planId())));
            positionRequest.setPlan(plan);
        }

        PositionRequest saved = positionRequestRepository.save(positionRequest);
        log.info("Created position request {} for department {} designation {}",
                saved.getId(), request.departmentId(), request.designationId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionRequestResponse> getPositionRequests(UUID departmentId) {
        return positionRequestRepository.findByDepartmentIdOrderByCreatedAtDesc(departmentId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PositionRequestResponse getPositionRequest(UUID id) {
        return mapper.toResponse(getPositionRequestOrThrow(id));
    }

    @Override
    public PositionRequestResponse updateStatus(UUID id, UpdatePositionRequestStatusRequest request) {
        PositionRequest positionRequest = getPositionRequestOrThrow(id);

        if (positionRequest.getStatus() == PositionRequestStatus.APPROVED
                || positionRequest.getStatus() == PositionRequestStatus.REJECTED) {
            throw new InvalidPlanStateException(
                    String.format(ErrorMessages.INVALID_STATUS_TRANSITION, positionRequest.getStatus()));
        }

        positionRequest.setStatus(request.status());
        positionRequest.setReviewedBy(request.reviewedBy());
        positionRequest.setReviewedAt(DateTimeUtil.now());

        PositionRequest saved = positionRequestRepository.save(positionRequest);
        log.info("Position request {} moved to status {} by {}", id, request.status(), request.reviewedBy());
        return mapper.toResponse(saved);
    }

    private PositionRequest getPositionRequestOrThrow(UUID id) {
        return positionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.POSITION_REQUEST_NOT_FOUND, id)));
    }
}
