package com.hospital.manpower.constants;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String ORGANIZATION_NOT_FOUND = "Organization not found for id: %s";
    public static final String LOCATION_NOT_FOUND = "Location not found for id: %s";
    public static final String HOSPITAL_NOT_FOUND = "Hospital not found for id: %s";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found for id: %s";
    public static final String DESIGNATION_NOT_FOUND = "Designation not found for id: %s";
    public static final String PLANNING_PERIOD_NOT_FOUND = "Planning period not found for id: %s";
    public static final String MANPOWER_PLAN_NOT_FOUND = "Manpower plan not found for id: %s";
    public static final String PLAN_DESIGNATION_NOT_FOUND = "Designation line item not found for id: %s in plan: %s";
    public static final String POSITION_REQUEST_NOT_FOUND = "Position request not found for id: %s";

    public static final String PLAN_ALREADY_EXISTS =
            "A manpower plan already exists for this hospital, department and planning period";
    public static final String ONLY_DRAFT_CAN_BE_MODIFIED = "Only plans in DRAFT status can be modified";
    public static final String ONLY_DRAFT_CAN_BE_SUBMITTED = "Only plans in DRAFT status can be submitted for approval";
    public static final String PLAN_MUST_HAVE_DESIGNATION = "A plan must have at least one designation before it can be submitted";
    public static final String INVALID_AMOUNT = "Amount must be greater than zero";
    public static final String INVALID_STATUS_TRANSITION = "Position request is already in a final state: %s";
}
