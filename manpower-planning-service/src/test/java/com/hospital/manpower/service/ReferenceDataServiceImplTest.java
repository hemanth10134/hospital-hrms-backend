package com.hospital.manpower.service;

import com.hospital.manpower.dto.request.CreateDesignationRequest;
import com.hospital.manpower.entity.*;
import com.hospital.manpower.exception.DuplicateReferenceDataException;
import com.hospital.manpower.exception.ResourceNotFoundException;
import com.hospital.manpower.mapper.ManpowerPlanMapper;
import com.hospital.manpower.repository.*;
import com.hospital.manpower.service.impl.ReferenceDataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
                .isInstanceOf(DuplicateReferenceDataException.class);
    }

    @Test
    void createDesignation_rejectsUnknownOrganization() {
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createDesignation(new CreateDesignationRequest(organizationId, "DIETICIAN", "Dietician")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createLocation_derivesCodeFromNameAndSaves() {
        Organization organization = new Organization();
        organization.setId(organizationId);
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(locationRepository.existsByOrganizationIdAndNameIgnoreCase(organizationId, "Uttar Pradesh")).thenReturn(false);
        when(locationRepository.existsByOrganizationIdAndCode(eq(organizationId), anyString())).thenReturn(false);
        when(locationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var created = service.createLocation(organizationId, "  Uttar Pradesh ");

        assertThat(created.name()).isEqualTo("Uttar Pradesh");
        assertThat(created.code()).isEqualTo("UTTAR-PRADESH");
    }

    @Test
    void createHospital_rejectsDuplicateNameWithinLocation() {
        UUID locationId = UUID.randomUUID();
        Location location = new Location();
        location.setId(locationId);
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(hospitalRepository.existsByLocationIdAndNameIgnoreCase(locationId, "West Delhi")).thenReturn(true);

        assertThatThrownBy(() -> service.createHospital(locationId, "West Delhi"))
                .isInstanceOf(DuplicateReferenceDataException.class)
                .hasMessageContaining("West Delhi");
        verify(hospitalRepository, never()).save(any());
    }

    @Test
    void createDepartment_suffixesCodeWhenSlugCollides() {
        UUID hospitalId = UUID.randomUUID();
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(hospital));
        when(departmentRepository.existsByHospitalIdAndNameIgnoreCase(hospitalId, "I.T.")).thenReturn(false);
        when(departmentRepository.existsByHospitalIdAndCode(hospitalId, "I-T")).thenReturn(true);
        when(departmentRepository.existsByHospitalIdAndCode(hospitalId, "I-T-2")).thenReturn(false);
        when(departmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.createDepartment(hospitalId, "I.T.").code()).isEqualTo("I-T-2");
    }

    @Test
    void getHierarchy_nestsLocationsHospitalsDepartmentsAndDesignationsUnderTheirOrganization() {
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Park Group");
        Location location = new Location();
        location.setId(UUID.randomUUID());
        location.setName("Delhi");
        location.setOrganization(organization);
        Hospital hospital = new Hospital();
        hospital.setId(UUID.randomUUID());
        hospital.setName("West Delhi");
        hospital.setLocation(location);
        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("Nursing");
        department.setHospital(hospital);
        Designation designation = new Designation();
        designation.setId(UUID.randomUUID());
        designation.setName("Nurse");
        designation.setOrganization(organization);

        when(organizationRepository.findAll()).thenReturn(List.of(organization));
        when(locationRepository.findAll()).thenReturn(List.of(location));
        when(hospitalRepository.findAll()).thenReturn(List.of(hospital));
        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(designationRepository.findAll()).thenReturn(List.of(designation));

        var tree = service.getHierarchy();

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).designations()).extracting("name").containsExactly("Nurse");
        var hospitalNode = tree.get(0).locations().get(0).hospitals().get(0);
        assertThat(hospitalNode.name()).isEqualTo("West Delhi");
        assertThat(hospitalNode.departments()).extracting("name").containsExactly("Nursing");
    }
}
