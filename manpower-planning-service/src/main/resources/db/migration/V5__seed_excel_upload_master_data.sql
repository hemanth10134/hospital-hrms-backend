-- Master data taken from the "manpowerPlanning - Excel Upload" sheet so its sample
-- rows (Delhi / Haryana / UP hospitals, West Delhi departments, Nurse designations)
-- resolve when uploaded. Idempotent: re-running never duplicates rows.

INSERT INTO locations (organization_id, code, name) VALUES
    ('11111111-1111-1111-1111-111111111111', 'HAR', 'Haryana'),
    ('11111111-1111-1111-1111-111111111111', 'UP', 'UP')
ON CONFLICT (organization_id, code) DO NOTHING;

INSERT INTO hospitals (location_id, code, name)
SELECT l.id, v.code, v.name
FROM (VALUES
          ('DEL', 'WEST-DELHI', 'West Delhi'),
          ('DEL', 'FARIDABAD', 'Faridabad'),
          ('HAR', 'SIGNATURE', 'Signature'),
          ('HAR', 'PALAM', 'Palam'),
          ('HAR', 'PANIPAT', 'Panipat'),
          ('HAR', 'SONIPAT', 'Sonipat'),
          ('HAR', 'KARNAL', 'Karnal'),
          ('UP', 'AGRA', 'Agra'),
          ('UP', 'BATHINDA', 'Bathinda'),
          ('UP', 'PATIALA', 'Patiala'),
          ('UP', 'KANPUR', 'Kanpur')
     ) AS v (location_code, code, name)
JOIN locations l ON l.code = v.location_code AND l.organization_id = '11111111-1111-1111-1111-111111111111'
ON CONFLICT (location_id, code) DO NOTHING;

INSERT INTO departments (hospital_id, code, name)
SELECT h.id, v.code, v.name
FROM (VALUES
          ('NURSING', 'Nursing'),
          ('HR', 'HR'),
          ('IT', 'IT'),
          ('TOP-MANAGEMENT', 'Top Management')
     ) AS v (code, name)
JOIN hospitals h ON h.code = 'WEST-DELHI'
JOIN locations l ON l.id = h.location_id AND l.code = 'DEL'
ON CONFLICT (hospital_id, code) DO NOTHING;

INSERT INTO designations (organization_id, code, name) VALUES
    ('11111111-1111-1111-1111-111111111111', 'NURSE', 'Nurse'),
    ('11111111-1111-1111-1111-111111111111', 'SR-NURSE', 'Sr Nurse'),
    ('11111111-1111-1111-1111-111111111111', 'TEAM-LEAD', 'Team Lead')
ON CONFLICT (organization_id, code) DO NOTHING;
