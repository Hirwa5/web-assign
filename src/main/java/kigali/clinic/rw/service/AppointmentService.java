package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public String saveAppointment(Appointment appointment) {
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null)
            return "Patient is required";

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null)
            return "Doctor is required";

        if (appointment.getAppointmentDate() == null)
            return "Appointment date is required";

        if (appointment.getAppointmentDate().toLocalDate().isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";

        if (patientRepo.findById(appointment.getPatient().getId()).isEmpty())
            return "Patient not found";

        if (doctorRepo.findById(appointment.getDoctor().getId()).isEmpty())
            return "Doctor not found";

        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED))
            return "Doctor is already booked on that date";

        if (appointmentRepo.findByPatientIdAndDoctorIdAndAppointmentDate(
                appointment.getPatient().getId(),
                appointment.getDoctor().getId(),
                appointment.getAppointmentDate()).isPresent())
            return "Appointment already exists for this patient, doctor and date";

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepo.findById(id);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(LocalDate start, LocalDate end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(Date.valueOf(start), Date.valueOf(end));
    }

    public List<Object[]> countAppointmentsByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    @Transactional
    public String cancelDoctorDay(UUID doctorId, LocalDate date) {
        int cancelled = appointmentRepo.cancelDoctorDay(doctorId, Date.valueOf(date), AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return cancelled + " appointments cancelled";
    }

    public Object getAppointmentsByPatientId(UUID patientId) {
        if (patientRepo.findById(patientId).isEmpty())
            return "Patient not found";
        return appointmentRepo.findByPatientId(patientId);
    }

    public String changeStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) return "The appointment with that id does not exist";

        AppointmentStatus current = existing.get().getStatus();

        boolean allowed = (current == AppointmentStatus.SCHEDULED && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED))
                || (current == AppointmentStatus.CONFIRMED && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED));

        if (!allowed)
            return "Cannot change status from " + current + " to " + newStatus;

        existing.get().setStatus(newStatus);
        appointmentRepo.save(existing.get());
        return "Appointment status updated successfully";
    }

    public String reschedule(UUID id, LocalDate newDate) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) return "The appointment with that id does not exist";

        Appointment appointment = existing.get();

        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            return "A COMPLETED appointment cannot be rescheduled";

        if (appointment.getStatus() == AppointmentStatus.CANCELLED)
            return "A CANCELLED appointment cannot be rescheduled";

        if (newDate.isBefore(LocalDate.now()))
            return "Appointment date cannot be in the past";

        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndIdNotAndStatusNot(
                appointment.getDoctor().getId(), Date.valueOf(newDate), id, AppointmentStatus.CANCELLED))
            return "Doctor is already booked on that date";

        appointment.setAppointmentDate(Date.valueOf(newDate));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    public String updateAppointment(UUID id, Appointment updated) {
        Optional<Appointment> existing = appointmentRepo.findById(id);
        if (existing.isEmpty()) {
            return "Appointment not found";
        }
        Appointment appointment = existing.get();
        appointment.setAppointmentDate(updated.getAppointmentDate());
        appointment.setReason(updated.getReason());
        appointment.setDoctor(updated.getDoctor());
        appointment.setPatient(updated.getPatient());
        appointmentRepo.save(appointment);
        return "Appointment updated successfully";
    }

    public String deleteAppointment(UUID id) {
        if (appointmentRepo.findById(id).isEmpty())
            return "Appointment not found";
        if (prescriptionRepo.existsByAppointmentId(id))
            return "That appointment has prescriptions, delete them first";
        appointmentRepo.deleteById(id);
        return "Appointment deleted successfully";
    }
}
