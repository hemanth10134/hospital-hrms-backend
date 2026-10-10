-- Supports: Planned/Not Planned designation status, current staff on position requests,
-- a hard cap on operating/working hours, and a guard against raising more than one
-- open (non-final) position request for the same department + designation.

ALTER TABLE manpower_plan_designations
    ADD COLUMN planned BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE position_requests
    ADD COLUMN current_staff INTEGER NOT NULL DEFAULT 0 CHECK (current_staff >= 0);

ALTER TABLE manpower_plans
    ADD CONSTRAINT chk_department_operating_hours_max CHECK (department_operating_hours <= 24),
    ADD CONSTRAINT chk_employee_working_hours_max CHECK (employee_working_hours <= 24);

-- One open (pending/under review) request per department + designation at a time;
-- a new request can only be raised again once the previous one is approved/rejected.
CREATE UNIQUE INDEX idx_position_requests_open_unique
    ON position_requests (department_id, designation_id)
    WHERE status IN ('PENDING_APPROVAL', 'UNDER_REVIEW');
