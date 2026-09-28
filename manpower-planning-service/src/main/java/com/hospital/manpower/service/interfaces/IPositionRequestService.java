package com.hospital.manpower.service.interfaces;

import com.hospital.manpower.dto.request.CreatePositionRequestRequest;
import com.hospital.manpower.dto.request.UpdatePositionRequestStatusRequest;
import com.hospital.manpower.dto.response.PositionRequestResponse;

import java.util.List;
import java.util.UUID;

public interface IPositionRequestService {

    PositionRequestResponse createPositionRequest(CreatePositionRequestRequest request);

    List<PositionRequestResponse> getPositionRequests(UUID departmentId);

    PositionRequestResponse getPositionRequest(UUID id);

    PositionRequestResponse updateStatus(UUID id, UpdatePositionRequestStatusRequest request);
}
