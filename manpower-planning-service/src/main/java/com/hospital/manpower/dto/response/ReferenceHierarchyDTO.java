package com.hospital.manpower.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * The full organization → location → hospital → department tree plus each
 * organization's designations, used by the Excel template and upload to resolve
 * names to ids without one request per dropdown level.
 */
public record ReferenceHierarchyDTO(
        UUID id,
        String code,
        String name,
        List<LookupDTO> designations,
        List<LocationNode> locations
) {
    public record LocationNode(UUID id, String code, String name, List<HospitalNode> hospitals) {
    }

    public record HospitalNode(UUID id, String code, String name, List<LookupDTO> departments) {
    }
}
