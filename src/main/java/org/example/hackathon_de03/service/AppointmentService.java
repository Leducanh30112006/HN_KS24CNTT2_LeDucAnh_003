package org.example.hackathon_de03.service;

import lombok.RequiredArgsConstructor;
import org.example.hackathon_de03.model.constant.AppointmentStatus;
import org.example.hackathon_de03.model.dto.AppointmentDetailDto;
import org.example.hackathon_de03.model.dto.AppointmentResponse;
import org.example.hackathon_de03.model.dto.DoctorBookingItem;
import org.example.hackathon_de03.model.entity.Appointment;
import org.example.hackathon_de03.model.entity.AppointmentDetail;
import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Patient;
import org.example.hackathon_de03.repository.AppointmentDetailRepository;
import org.example.hackathon_de03.repository.AppointmentRepository;
import org.example.hackathon_de03.repository.DoctorRepository;
import org.example.hackathon_de03.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentDetailRepository appointmentDetailRepository;

    @Transactional(rollbackFor = Exception.class)
    public AppointmentResponse bookAppointment(String patientPhone, String patientName, List<DoctorBookingItem> items) {
        if (patientPhone == null || patientPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sách đặt khám không được để trống.");
        }

        List<Doctor> matchedDoctors = new ArrayList<>();
        for (DoctorBookingItem item : items) {
            if (item.getDoctorName() == null || item.getDoctorName().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên bác sĩ không được để trống.");
            }
            int quantity = (item.getQuantity() != null && item.getQuantity() > 0) ? item.getQuantity() : 1;

            Doctor doctor = findDoctorByNameFlexible(item.getDoctorName());
            if (doctor == null) {
                throw new IllegalArgumentException("Không tìm thấy bác sĩ: " + item.getDoctorName());
            }

            int availableStock = (doctor.getStock() != null) ? doctor.getStock() : 0;
            if (availableStock < quantity) {
                throw new IllegalArgumentException(
                        String.format("Bác sĩ %s chỉ còn %d lượt khám, không đủ số lượng %d yêu cầu.",
                                doctor.getName(), availableStock, quantity)
                );
            }

            matchedDoctors.add(doctor);
        }

        String cleanPhone = patientPhone.trim();
        String cleanName = (patientName != null && !patientName.trim().isEmpty()) ? patientName.trim() : "Bệnh nhân " + cleanPhone;

        Patient patient = patientRepository.findByPhone(cleanPhone)
                .map(existing -> {
                    if (cleanName != null && !cleanName.equals(existing.getFullName()) && !cleanName.startsWith("Bệnh nhân ")) {
                        existing.setFullName(cleanName);
                        return patientRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> patientRepository.save(new Patient(null, cleanName, cleanPhone, null, null)));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setAppointmentDate(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setTotalAmount(BigDecimal.ZERO);
        appointment.setNote("Đặt qua AI Chatbot");
        appointment = appointmentRepository.save(appointment);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<AppointmentDetailDto> detailDtos = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            DoctorBookingItem item = items.get(i);
            Doctor doctor = matchedDoctors.get(i);
            int quantity = (item.getQuantity() != null && item.getQuantity() > 0) ? item.getQuantity() : 1;

            doctor.setStock(doctor.getStock() - quantity);
            doctorRepository.save(doctor);

            BigDecimal unitPrice = doctor.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            AppointmentDetail detail = new AppointmentDetail();
            detail.setAppointment(appointment);
            detail.setDoctor(doctor);
            detail.setQuantity(quantity);
            detail.setUnitPrice(unitPrice);
            appointmentDetailRepository.save(detail);

            detailDtos.add(AppointmentDetailDto.builder()
                    .doctorName(doctor.getName())
                    .specialty(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "")
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build());
        }

        appointment.setTotalAmount(totalAmount);
        appointmentRepository.save(appointment);

        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .patientName(patient.getFullName())
                .patientPhone(patient.getPhone())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .totalAmount(totalAmount)
                .note(appointment.getNote())
                .details(detailDtos)
                .message("Đặt lịch khám thành công cho bệnh nhân " + patient.getFullName())
                .build();
    }

    private Doctor findDoctorByNameFlexible(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String cleanName = name.trim();
        return doctorRepository.findByNameIgnoreCase(cleanName)
                .or(() -> {
                    List<Doctor> list = doctorRepository.searchDoctorByName(cleanName);
                    return list.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(list.get(0));
                })
                .or(() -> {
                    List<Doctor> list = doctorRepository.findByNameContainingIgnoreCase(cleanName);
                    return list.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(list.get(0));
                })
                .orElse(null);
    }
}
