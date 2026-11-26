package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping(value = {"/api/doctor", "/api/doctors"})
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor) {
        String message = doctorService.saveDoctor(doctor);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/by-specialization")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.getDoctorsBySpecialization(name), HttpStatus.OK);
    }

    @GetMapping(value = "/without-office")
    public ResponseEntity<List<Doctor>> getDoctorsWithoutOffice() {
        return new ResponseEntity<>(doctorService.getDoctorsWithoutOffice(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .<ResponseEntity<?>>map(doctor -> new ResponseEntity<>(doctor, HttpStatus.OK))
                .orElse(new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String message = doctorService.updateDoctor(id, doctor);
        if (message.equals("Doctor updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String message = doctorService.addSpecialization(doctorId, specializationId);
        if (message.equals("Specialization added to the doctor")) return new ResponseEntity<>(message, HttpStatus.OK);
        if (message.equals("The doctor already has that specialization")) return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId) {
        String message = doctorService.removeSpecialization(doctorId, specializationId);
        if (message.equals("Specialization removed from the doctor")) return new ResponseEntity<>(message, HttpStatus.OK);
        if (message.equals("The doctor does not have that specialization")) return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(value = "/{doctorId}/office")
    public ResponseEntity<?> moveToOffice(@PathVariable UUID doctorId, @RequestParam int officeNumber) {
        String message = doctorService.moveToOffice(doctorId, officeNumber);
        if (message.equals("Office changed successfully")) return new ResponseEntity<>(message, HttpStatus.OK);
        if (message.equals("That office is taken by other doctor")) return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/{doctorId}/assign-office/{officeId}")
    public ResponseEntity<?> assignOffice(@PathVariable UUID doctorId, @PathVariable UUID officeId) {
        String message = doctorService.assignOffice(doctorId, officeId);
        if (message.equals("Office assigned successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        if (message.equals("Office is already assigned to another doctor")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String message = doctorService.deleteDoctor(id);
        if (message.equals("Doctor deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{doctorId}/agenda")
    public ResponseEntity<?> getAgenda(@PathVariable UUID doctorId, @RequestParam String date) {
        Object result = doctorService.getAgenda(doctorId, LocalDate.parse(date));
        if (result instanceof String) return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<?> searchByName(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.searchByName(name), HttpStatus.OK);
    }

    @GetMapping(value = "/workload")
    public ResponseEntity<?> getDoctorWorkload(@RequestParam String start, @RequestParam String end) {
        return new ResponseEntity<>(doctorService.getDoctorWorkload(LocalDate.parse(start), LocalDate.parse(end)), HttpStatus.OK);
    }
}
