-- Manpower Planning module schema.
-- Reference/master data (organization, location, hospital, department, designation)
-- is owned by the Org/Core service in the full platform; this service keeps a local,
-- read-only copy of the subset it needs so the module can run independently for now.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE organizations (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code          VARCHAR(30) NOT NULL UNIQUE,
    name          VARCHAR(150) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE locations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    code            VARCHAR(30) NOT NULL,
    name            VARCHAR(150) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (organization_id, code)
);

CREATE TABLE hospitals (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id  UUID NOT NULL REFERENCES locations(id),
    code         VARCHAR(30) NOT NULL,
    name         VARCHAR(150) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (location_id, code)
);

CREATE TABLE departments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hospital_id  UUID NOT NULL REFERENCES hospitals(id),
    code         VARCHAR(30) NOT NULL,
    name         VARCHAR(150) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (hospital_id, code)
);

CREATE TABLE designations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    code            VARCHAR(40) NOT NULL,
    name            VARCHAR(150) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (organization_id, code)
);

CREATE TABLE planning_periods (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code         VARCHAR(30) NOT NULL UNIQUE,
    label        VARCHAR(60) NOT NULL,
    start_date   DATE NOT NULL,
    end_date     DATE NOT NULL,
    CHECK (end_date > start_date)
);

-- Manpower plan header: one plan per hospital + department + planning period.
CREATE TABLE manpower_plans (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id             UUID NOT NULL REFERENCES organizations(id),
    location_id                 UUID NOT NULL REFERENCES locations(id),
    hospital_id                 UUID NOT NULL REFERENCES hospitals(id),
    department_id               UUID NOT NULL REFERENCES departments(id),
    planning_period_id          UUID NOT NULL REFERENCES planning_periods(id),
    number_of_beds              INTEGER NOT NULL CHECK (number_of_beds >= 0),
    department_operating_hours  NUMERIC(4,1) NOT NULL CHECK (department_operating_hours > 0),
    employee_working_hours      NUMERIC(4,1) NOT NULL CHECK (employee_working_hours > 0),
    status                      VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_by                  VARCHAR(150) NOT NULL,
    submitted_by                VARCHAR(150),
    submitted_at                TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED')),
    UNIQUE (hospital_id, department_id, planning_period_id)
);

-- Designation-wise staffing line items belonging to a plan.
CREATE TABLE manpower_plan_designations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id             UUID NOT NULL REFERENCES manpower_plans(id) ON DELETE CASCADE,
    designation_id      UUID NOT NULL REFERENCES designations(id),
    staffing_ratio      NUMERIC(8,2) NOT NULL CHECK (staffing_ratio > 0),
    monthly_salary      NUMERIC(12,2) NOT NULL CHECK (monthly_salary >= 0),
    leave_buffer_pct    NUMERIC(5,2) NOT NULL DEFAULT 0 CHECK (leave_buffer_pct >= 0),
    current_staff       INTEGER NOT NULL DEFAULT 0 CHECK (current_staff >= 0),
    display_order       INTEGER NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (plan_id, designation_id)
);

-- Additional position requests raised beyond the planned requirement, subject to approval.
CREATE TABLE position_requests (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id                 UUID REFERENCES manpower_plans(id),
    department_id           UUID NOT NULL REFERENCES departments(id),
    designation_id          UUID NOT NULL REFERENCES designations(id),
    requested_positions     INTEGER NOT NULL CHECK (requested_positions > 0),
    reason                  TEXT NOT NULL,
    additional_monthly_budget NUMERIC(12,2) NOT NULL CHECK (additional_monthly_budget >= 0),
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    requested_by            VARCHAR(150) NOT NULL,
    reviewed_by             VARCHAR(150),
    reviewed_at             TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (status IN ('PENDING_APPROVAL', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_manpower_plan_designations_plan ON manpower_plan_designations(plan_id);
CREATE INDEX idx_position_requests_department ON position_requests(department_id);
CREATE INDEX idx_manpower_plans_hospital_department ON manpower_plans(hospital_id, department_id);
