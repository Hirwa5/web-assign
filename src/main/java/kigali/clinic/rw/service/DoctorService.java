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
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) return "The doctor with that id does not exist";

        Optional<Specialization> specOpt = specRepo.findById(specializationId);
        if (specOpt.isEmpty()) return "That specialization does not exist";

        Specialization specialization = specOpt.get();
        if (specialization.getDoctors().stream().anyMatch(d -> d.getId().equals(doctorId)))
            return "The doctor already has that specialization";

        specialization.getDoctors().add(doctorOpt.get());
        specRepo.save(specialization);
        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) return "The doctor with that id does not exist";

        Optional<Specialization> specOpt = specRepo.findById(specializationId);
        if (specOpt.isEmpty()) return "That specialization does not exist";

        Specialization specialization = specOpt.get();
        boolean had = specialization.getDoctors().removeIf(d -> d.getId().equals(doctorId));
        if (!had) return "The doctor does not have that specialization";

        specRepo.save(specialization);
        return "Specialization removed from the doctor";
    }

    public String moveToOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) return "The doctor with that id does not exist";

        Optional<Office> officeOpt = officeRepo.findByOfficeNumber(officeNumber);
        if (officeOpt.isEmpty()) return "The office with that office number does not exist";

        if (doctorRepo.existsByOfficeOfficeNumberAndIdNot(officeNumber, doctorId))
            return "That office is taken by other doctor";

        Doctor doctor = doctorOpt.get();
        doctor.setOffice(officeOpt.get());
        doctorRepo.save(doctor);
        return "Office changed successfully";
    }

    public String saveDoctor(Doctor doctor) {
        doctorRepo.save(doctor);
        return "Doctor saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepo.findById(id);
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findDoctorsBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public String updateDoctor(UUID id, Doctor updated) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor not found";
        }
        Doctor doctor = existing.get();
        doctor.setFirstName(updated.getFirstName());
        doctor.setLastName(updated.getLastName());
        doctor.setDateOfBirth(updated.getDateOfBirth());
        doctor.setOffice(updated.getOffice());
        doctorRepo.save(doctor);
        return "Doctor updated successfully";
    }

    public String assignOffice(UUID doctorId, UUID officeId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) return "Doctor not found";

        Optional<Office> officeOpt = officeRepo.findById(officeId);
        if (officeOpt.isEmpty()) return "Office not found";

        Optional<Doctor> occupant = doctorRepo.findByOfficeId(officeId);
        if (occupant.isPresent() && !occupant.get().getId().equals(doctorId)) {
            return "Office is already assigned to another doctor";
        }

        Doctor doctor = doctorOpt.get();
        doctor.setOffice(officeOpt.get());
        doctorRepo.save(doctor);
        return "Office assigned successfully";
    }

    public String deleteDoctor(UUID id) {
        if (doctorRepo.findById(id).isEmpty()) {
            return "Doctor not found";
        }
        doctorRepo.deleteById(id);
        return "Doctor deleted successfully";
    }

    public Object getAgenda(UUID doctorId, LocalDate date) {
        if (doctorRepo.findById(doctorId).isEmpty()) return "The doctor with that id does not exist";
        return appointmentRepo.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, Date.valueOf(date), AppointmentStatus.CANCELLED);
    }

    public List<Doctor> searchByName(String name) {
        return doctorRepo.searchByName(name);
    }

    public List<Object[]> getDoctorWorkload(LocalDate start, LocalDate end) {
        return doctorRepo.findDoctorWorkload(Date.valueOf(start), Date.valueOf(end));
    }
}
