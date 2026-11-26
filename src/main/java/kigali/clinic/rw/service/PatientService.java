package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePatient(Patient patient) {
        patientRepo.save(patient);
        return "Patient saved successfully";
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepo.findById(id);
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public Object getPatientsOfDoctor(UUID doctorId) {
        if (doctorRepo.findById(doctorId).isEmpty())
            return "The doctor with that id does not exist";
        return patientRepo.findPatientsOfDoctor(doctorId);
    }

    public List<Patient> getFrequentPatients(long min) {
        return patientRepo.findFrequentPatients(min);
    }

    public String updatePatient(UUID id, Patient updated) {
        Optional<Patient> existing = patientRepo.findById(id);
        if (existing.isEmpty()) {
            return "Patient not found";
        }
        Patient patient = existing.get();
        patient.setFirstName(updated.getFirstName());
        patient.setLastName(updated.getLastName());
        patient.setDateOfBirth(updated.getDateOfBirth());
        patientRepo.save(patient);
        return "Patient updated successfully";
    }

    public String deletePatient(UUID id) {
        if (patientRepo.findById(id).isEmpty()) {
            return "Patient not found";
        }
        patientRepo.deleteById(id);
        return "Patient deleted successfully";
    }

    public Object getUpcomingAppointments(String ssNumber) {
        if (patientRepo.findBySsNumber(ssNumber).isEmpty())
            return "That patient with that ssnumber does not exist";
        return appointmentRepo.findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                ssNumber, Date.valueOf(LocalDate.now()),
                Arrays.asList(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }
}
