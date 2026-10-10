package com.hospital.manpower.service;

import com.hospital.manpower.dto.request.CreateDesignationRequest;
import com.hospital.manpower.entity.Organization;
import com.hospital.manpower.exception.DesignationAlreadyExistsException;
import com.hospital.manpower.exception.ResourceNotFoundException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.impl.ReferenceDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DesignationRepository designationRepository;
    @Mock
    private PlanningPeriodRepository planningPeriodRepository;

    private ReferenceDataServiceImpl service;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        service = new ReferenceDataServiceImpl(
                organizationRepository, locationRepository, hospitalRepository,
                departmentRepository, designationRepository, planningPeriodRepository, new ManpowerPlanMapper());
        organizationId = UUID.randomUUID();
    }

    @Test
    void createDesignation_savesNewDesignationForOrganization() {
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setCode("PARK-GROUP");
        organization.setName("Park Group");

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(designationRepository.existsByOrganizationIdAndCode(organizationId, "DIETICIAN")).thenReturn(false);
        when(designationRepository.save(any())).thenAnswer(invocation -> {
            var designation = invocation.getArgument(0, com.hospital.manpower.entity.Designation.class);
            designation.setId(UUID.randomUUID());
            return designation;
        });

        var result = service.createDesignation(new CreateDesignationRequest(organizationId, "DIETICIAN", "Dietician"));

        assertThat(result.code()).isEqualTo("DIETICIAN");
        assertThat(result.name()).isEqualTo("Dietician");
    }

    @Test
    void createDesignation_rejectsDuplicateCodeForSameOrganization() {
        Organization organization = new Organization();
        organization.setId(organizationId);
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(designationRepository.existsByOrganizationIdAndCode(organizationId, "DIETICIAN")).thenReturn(true);

        assertThatThrownBy(() -> service.createDesignation(new CreateDesignationRequest(organizationId, "DIETICIAN", "Dietician")))
                .isInstanceOf(DesignationAlreadyExistsException.class);
    }

    @Test
    void createDesignation_rejectsUnknownOrganization() {
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createDesignation(new CreateDesignationRequest(organizationId, "DIETICIAN", "Dietician")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
