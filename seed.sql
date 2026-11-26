-- ============================================================
-- CLINIC SEED DATA - auto-generated UUIDs
-- ============================================================

-- OFFICES
INSERT INTO office (id, name, office_number) VALUES
  (gen_random_uuid(), 'General Consultation', 101),
  (gen_random_uuid(), 'Cardiology Room',      102),
  (gen_random_uuid(), 'Pediatrics Room',      103),
  (gen_random_uuid(), 'Emergency Room',       104);

-- SPECIALIZATIONS
INSERT INTO specialization (id, name) VALUES
  (gen_random_uuid(), 'General Medicine'),
  (gen_random_uuid(), 'Cardiology'),
  (gen_random_uuid(), 'Pediatrics');

-- DOCTORS (each linked to an office by office_number lookup)
INSERT INTO doctor (id, first_name, last_name, date_of_birth, office_id) VALUES
  (gen_random_uuid(), 'Jean',   'Mugisha',   '1980-03-15', (SELECT id FROM office WHERE office_number = 101)),
  (gen_random_uuid(), 'Alice',  'Uwimana',   '1985-07-22', (SELECT id FROM office WHERE office_number = 102)),
  (gen_random_uuid(), 'Pierre', 'Habimana',  '1990-11-05', (SELECT id FROM office WHERE office_number = 103));

-- DOCTOR_SPECIALIZATION
INSERT INTO doctor_specialization (specialization_id, doctor_id) VALUES
  ((SELECT id FROM specialization WHERE name = 'General Medicine'), (SELECT id FROM doctor WHERE last_name = 'Mugisha')),
  ((SELECT id FROM specialization WHERE name = 'Cardiology'),       (SELECT id FROM doctor WHERE last_name = 'Mugisha')),
  ((SELECT id FROM specialization WHERE name = 'Cardiology'),       (SELECT id FROM doctor WHERE last_name = 'Uwimana')),
  ((SELECT id FROM specialization WHERE name = 'Pediatrics'),       (SELECT id FROM doctor WHERE last_name = 'Uwimana')),
  ((SELECT id FROM specialization WHERE name = 'Pediatrics'),       (SELECT id FROM doctor WHERE last_name = 'Habimana'));

-- PATIENTS
INSERT INTO patient (id, first_name, last_name, date_of_birth, ss_number) VALUES
  (gen_random_uuid(), 'Marie',   'Ingabire',   '1995-01-10', 'SS-001'),
  (gen_random_uuid(), 'Eric',    'Nkurunziza', '1988-06-25', 'SS-002'),
  (gen_random_uuid(), 'Grace',   'Mutoni',     '2000-09-14', 'SS-003'),
  (gen_random_uuid(), 'Patrick', 'Bizimana',   '1975-12-30', 'SS-004'),
  (gen_random_uuid(), 'Diane',   'Uwase',      '1992-04-18', 'SS-005');

-- APPOINTMENTS
INSERT INTO appointment (id, appointment_date, reason, status, patient_id, doctor_id) VALUES
  (gen_random_uuid(), '2026-10-20', 'Regular checkup',    'SCHEDULED', (SELECT id FROM patient WHERE ss_number = 'SS-001'), (SELECT id FROM doctor WHERE last_name = 'Mugisha')),
  (gen_random_uuid(), '2026-10-22', 'Follow-up visit',    'CONFIRMED',  (SELECT id FROM patient WHERE ss_number = 'SS-001'), (SELECT id FROM doctor WHERE last_name = 'Mugisha')),
  (gen_random_uuid(), '2026-10-30', 'Cardiology consult', 'SCHEDULED', (SELECT id FROM patient WHERE ss_number = 'SS-001'), (SELECT id FROM doctor WHERE last_name = 'Uwimana')),
  (gen_random_uuid(), '2026-10-20', 'Cancelled visit',    'CANCELLED', (SELECT id FROM patient WHERE ss_number = 'SS-002'), (SELECT id FROM doctor WHERE last_name = 'Mugisha')),
  (gen_random_uuid(), '2026-10-01', 'Past consultation',  'COMPLETED', (SELECT id FROM patient WHERE ss_number = 'SS-003'), (SELECT id FROM doctor WHERE last_name = 'Mugisha')),
  (gen_random_uuid(), '2026-10-05', 'Past consultation',  'COMPLETED', (SELECT id FROM patient WHERE ss_number = 'SS-004'), (SELECT id FROM doctor WHERE last_name = 'Uwimana'));
