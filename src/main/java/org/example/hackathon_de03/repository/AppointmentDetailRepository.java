package org.example.hackathon_de03.repository;

import org.example.hackathon_de03.model.entity.AppointmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDetailRepository extends JpaRepository<AppointmentDetail, Long> {
}
