package com.hospital.manpower.service;

import com.hospital.manpower.dto.request.CreatePositionRequestRequest;
import com.hospital.manpower.entity.Department;
import com.hospital.manpower.entity.Designation;
import com.hospital.manpower.enums.PositionRequestStatus;
import com.hospital.manpower.exception.DuplicatePositionRequestException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.DepartmentRepository;
import com.hospital.manpower.repository.DesignationRepository;
import com.hospital.manpower.repository.ManpowerPlanRepository;
import com.hospital.manpower.repository.PositionRequestRepository;
import com.hospital.manpower.service.impl.PositionRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionRequestServiceImplTest {

    @Mock
    private PositionRequestRepository positionRequestRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DesignationRepository designationRepository;
    @Mock
    private ManpowerPlanRepository manpowerPlanRepository;

    private PositionRequestServiceImpl service;

    private UUID departmentId;
    private UUID designationId;

    @BeforeEach
    void setUp() {
        service = new PositionRequestServiceImpl(
                positionRequestRepository, departmentRepository, designationRepository,
                manpowerPlanRepository, new ManpowerPlanMapper());

        departmentId = UUID.randomUUID();
        designationId = UUID.randomUUID();
    }

    @Test
    void createPositionRequest_rejectsWhenAnOpenRequestAlreadyExistsForSameDepartmentAndDesignation() {
        Department department = new Department();
        department.setId(departmentId);
        Designation designation = new Designation();
        designation.setId(designationId);

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(designationRepository.findById(designationId)).thenReturn(Optional.of(designation));
        when(positionRequestRepository.existsByDepartmentIdAndDesignationIdAndStatusIn(
                departmentId, designationId,
                java.util.Set.of(PositionRequestStatus.PENDING_APPROVAL, PositionRequestStatus.UNDER_REVIEW)))
                .thenReturn(true);

        CreatePositionRequestRequest request = new CreatePositionRequestRequest(
                null, departmentId, designationId, 2, 1, "Growing patient load", BigDecimal.valueOf(50000), "sunita.sharma");

        assertThatThrownBy(() -> service.createPositionRequest(request))
                .isInstanceOf(DuplicatePositionRequestException.class);
    }
}
