package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> appointmentOpt = appointmentRepo.findById(appointmentId);
        if (appointmentOpt.isEmpty()) return "The appointment with that id does not exist";

        if (appointmentOpt.get().getStatus() != AppointmentStatus.COMPLETED)
            return "Prescriptions are only for COMPLETED appointments";

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointmentOpt.get());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public Object getPrescriptionsByAppointment(UUID appointmentId) {
        if (appointmentRepo.findById(appointmentId).isEmpty())
            return "The appointment with that id does not exist";
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getPrescriptionsByPatientSsNumber(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumber(ssNumber);
    }

    public List<Object[]> getTopMedicines() {
        return prescriptionRepo.findTopMedicines();
    }

    public String updatePrescription(UUID id, Prescription updated) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);
        if (existing.isEmpty()) return "The prescription with that id does not exist";

        Prescription prescription = existing.get();
        prescription.setMedicineName(updated.getMedicineName());
        prescription.setDosage(updated.getDosage());
        prescription.setDurationDays(updated.getDurationDays());
        prescriptionRepo.save(prescription);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        if (prescriptionRepo.findById(id).isEmpty())
            return "The prescription with that id does not exist";
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    public boolean existsByAppointmentId(UUID appointmentId) {
        return prescriptionRepo.existsByAppointmentId(appointmentId);
    }
}
