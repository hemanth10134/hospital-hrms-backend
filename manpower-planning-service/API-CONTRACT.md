# Manpower Planning Service - API Contract

Base URL: `http://localhost:8081/api/v1`

Interactive docs (once running): `http://localhost:8081/swagger-ui.html`

All IDs are UUID strings. All money fields are decimal (INR). Timestamps are ISO-8601.

## Derived staffing calculation

`requiredStaff`, `vacancies`, `excess` and `monthlyBudget` on each designation line are
**computed on every read**, never stored, so edits to beds/ratio/leave buffer are always
reflected immediately:

```
shiftsPerDay  = departmentOperatingHours / employeeWorkingHours
baseStaff     = numberOfBeds / staffingRatio         (staffingRatio = beds per staff)
requiredStaff = ceil(baseStaff * shiftsPerDay * (1 + leaveBufferPct / 100))
vacancies     = max(requiredStaff - currentStaff, 0)
excess        = max(currentStaff - requiredStaff, 0)
monthlyBudget = requiredStaff * monthlySalary
```

Department summary aggregates: `totalRequiredStaff` and `currentStaff` are sums across all
lines; `openPositions = totalRequiredStaff - currentStaff`; `estimatedMonthlyBudget` is the
sum of each line's `monthlyBudget`.

---

## Reference data (dropdowns)

| Method | Path | Description |
|---|---|---|
| GET | `/organizations` | List organizations |
| GET | `/organizations/{organizationId}/locations` | Locations under an organization |
| GET | `/locations/{locationId}/hospitals` | Hospitals under a location |
| GET | `/hospitals/{hospitalId}/departments` | Departments under a hospital |
| GET | `/organizations/{organizationId}/designations` | Designation master for the org |
| GET | `/planning-periods` | Available planning periods |

Response shape (`LookupDTO`):
```json
{ "id": "uuid", "code": "ICU", "name": "ICU - Intensive Care Unit" }
```

---

## Manpower Plans

### Create plan (Save as Draft)
`POST /manpower-plans`

```json
{
  "organizationId": "uuid",
  "locationId": "uuid",
  "hospitalId": "uuid",
  "departmentId": "uuid",
  "planningPeriodId": "uuid",
  "numberOfBeds": 20,
  "departmentOperatingHours": 24,
  "employeeWorkingHours": 8,
  "createdBy": "Sunita Sharma",
  "designations": [
    {
      "designationId": "uuid",
      "staffingRatio": 10,
      "monthlySalary": 150000,
      "leaveBufferPct": 10,
      "currentStaff": 2
    }
  ]
}
```
* `201 Created` -> `ManpowerPlanResponse` (see below).
* `409 Conflict` if a plan already exists for this hospital + department + planning period.

### Get plan
`GET /manpower-plans/{planId}` -> `200 OK` -> `ManpowerPlanResponse`

```json
{
  "id": "uuid",
  "organization": { "id": "uuid", "code": "PARK-GROUP", "name": "Park Group" },
  "location": { "id": "uuid", "code": "DEL", "name": "Delhi" },
  "hospital": { "id": "uuid", "code": "PARK-MAIN", "name": "Park Hospital (Main)" },
  "department": { "id": "uuid", "code": "ICU", "name": "ICU - Intensive Care Unit" },
  "planningPeriod": { "id": "uuid", "code": "FY2026-27", "label": "Apr 2026 - Mar 2027", "startDate": "2026-04-01", "endDate": "2027-03-31" },
  "numberOfBeds": 20,
  "departmentOperatingHours": 24,
  "employeeWorkingHours": 8,
  "status": "DRAFT",
  "createdBy": "Sunita Sharma",
  "createdAt": "2026-09-28T10:00:00",
  "updatedAt": "2026-09-28T10:00:00",
  "designations": [
    {
      "id": "uuid",
      "designationId": "uuid",
      "designationName": "Consultant Intensivist",
      "staffingRatio": 10,
      "monthlySalary": 150000,
      "leaveBufferPct": 10,
      "requiredStaff": 7,
      "currentStaff": 2,
      "vacancies": 5,
      "excess": 0,
      "monthlyBudget": 1050000
    }
  ],
  "summary": {
    "totalRequiredStaff": 7,
    "currentStaff": 2,
    "openPositions": 5,
    "estimatedMonthlyBudget": 1050000
  }
}
```

### View previous plans
`GET /manpower-plans?hospitalId={id}&departmentId={id}` -> `200 OK` -> `ManpowerPlanSummaryListItem[]`

### Update plan (edit DRAFT only)
`PUT /manpower-plans/{planId}` — same body shape as create, minus the identifying fields
(department parameters + full designation list, replaced wholesale).
* `409 Conflict` if the plan is not in `DRAFT` status.

### Submit for approval
`POST /manpower-plans/{planId}/submit`
```json
{ "submittedBy": "Sunita Sharma" }
```
* `200 OK` -> plan with `status: "SUBMITTED"`.
* `409 Conflict` if not `DRAFT`, or if the plan has zero designation lines.

---

## Additional Position Requests

### Create request
`POST /position-requests`
```json
{
  "planId": "uuid",
  "departmentId": "uuid",
  "designationId": "uuid",
  "requestedPositions": 4,
  "reason": "Increase in bed capacity",
  "additionalMonthlyBudget": 180000,
  "requestedBy": "Sunita Sharma"
}
```
* `201 Created` -> `PositionRequestResponse`, `status: "PENDING_APPROVAL"`.

### List requests for a department
`GET /position-requests?departmentId={id}` -> `200 OK` -> `PositionRequestResponse[]`

### Get single request
`GET /position-requests/{id}` -> `200 OK` -> `PositionRequestResponse`

### Update status (approve / reject / mark under review)
`PATCH /position-requests/{id}/status`
```json
{ "status": "APPROVED", "reviewedBy": "Rajesh Kumar" }
```
* `status` one of `PENDING_APPROVAL | UNDER_REVIEW | APPROVED | REJECTED`.
* `409 Conflict` if the request is already `APPROVED` or `REJECTED` (final states).

---

## Error shape

```json
{
  "timestamp": "2026-09-28T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Manpower plan not found for id: ...",
  "details": []
}
```
`400` for validation errors (`details` lists each field failure), `404` for missing
resources, `409` for invalid state transitions.
