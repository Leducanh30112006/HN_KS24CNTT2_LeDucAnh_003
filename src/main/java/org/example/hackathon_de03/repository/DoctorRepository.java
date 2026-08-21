package org.example.hackathon_de03.repository;

import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> searchDoctorByName(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE d.specialty = :specialty")
    List<Doctor> searchDoctorBySpecialty(@Param("specialty") Specialty specialty);

    @Query("SELECT d FROM Doctor d JOIN d.specialty s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialtyName, '%'))")
    List<Doctor> searchDoctorBySpecialtyName(@Param("specialtyName") String specialtyName);

    Optional<Doctor> findByNameIgnoreCase(String name);

    List<Doctor> findByNameContainingIgnoreCase(String name);
}
