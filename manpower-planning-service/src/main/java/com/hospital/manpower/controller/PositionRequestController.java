package com.hospital.manpower.controller;

import com.hospital.manpower.dto.request.CreatePositionRequestRequest;
import com.hospital.manpower.dto.request.UpdatePositionRequestStatusRequest;
import com.hospital.manpower.dto.response.PositionRequestResponse;
import com.hospital.manpower.service.interfaces.IPositionRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/position-requests")
@RequiredArgsConstructor
public class PositionRequestController {

    private final IPositionRequestService positionRequestService;

    @PostMapping
    public ResponseEntity<PositionRequestResponse> create(@Valid @RequestBody CreatePositionRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionRequestService.createPositionRequest(request));
    }

    @GetMapping
    public List<PositionRequestResponse> getByDepartment(@RequestParam UUID departmentId) {
        return positionRequestService.getPositionRequests(departmentId);
    }

    @GetMapping("/{id}")
    public PositionRequestResponse getById(@PathVariable UUID id) {
        return positionRequestService.getPositionRequest(id);
    }

    @PatchMapping("/{id}/status")
    public PositionRequestResponse updateStatus(@PathVariable UUID id,
                                                 @Valid @RequestBody UpdatePositionRequestStatusRequest request) {
        return positionRequestService.updateStatus(id, request);
    }
}
