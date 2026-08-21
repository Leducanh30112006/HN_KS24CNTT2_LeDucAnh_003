package org.example.hackathon_de03.controller;

import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Specialty;
import org.example.hackathon_de03.repository.DoctorRepository;
import org.example.hackathon_de03.repository.SpecialtyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorController(DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy bác sĩ"));
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/searchName")
    public List<Doctor> searchDoctors(@RequestParam(required = false) String name, @RequestParam(required = false) String specialty) {
        if (name != null && !name.isEmpty()) {
            return doctorRepository.searchDoctorByName(name);
        } else if (specialty != null && !specialty.isEmpty()) {
            Specialty specialtyEntity = specialtyRepository.findByName(specialty);
            if (specialtyEntity != null) {
                return doctorRepository.searchDoctorBySpecialty(specialtyEntity);
            }
        } else {
            return doctorRepository.findAll();
        }
        return List.of();
    }

    @GetMapping("/searchSpecialty")
    public List<Doctor> searchDoctorsBySpecialty(@RequestParam String specialty) {
        Specialty specialtyEntity = specialtyRepository.findByName(specialty);
        if (specialtyEntity != null) {
            return doctorRepository.searchDoctorBySpecialty(specialtyEntity);
        }
        return List.of();
    }
}
