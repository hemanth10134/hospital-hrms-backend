package com.hospital.manpower.dto.response;

import java.util.UUID;

public record LookupDTO(UUID id, String code, String name) {
}
