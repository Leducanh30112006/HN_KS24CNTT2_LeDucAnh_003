package org.example.hackathon_de03.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Patient;
import org.example.hackathon_de03.model.entity.Specialty;
import org.example.hackathon_de03.repository.DoctorRepository;
import org.example.hackathon_de03.repository.PatientRepository;
import org.example.hackathon_de03.repository.SpecialtyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseInitializeService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @PostConstruct
    public void initializeDatabase() {
        if (specialtyRepository.count() == 0) {
            Specialty noiTongQuat = specialtyRepository.save(new Specialty(null, "Nội tổng quát", "Khám và điều trị các bệnh lý nội khoa"));
            Specialty nhiKhoa = specialtyRepository.save(new Specialty(null, "Nhi khoa", "Chăm sóc sức khỏe trẻ em"));
            Specialty daLieu = specialtyRepository.save(new Specialty(null, "Da liễu", "Khám và điều trị các bệnh về da"));
            Specialty taiMuiHong = specialtyRepository.save(new Specialty(null, "Tai Mũi Họng", "Nội soi và điều trị tai mũi họng"));
            Specialty sanPhuKhoa = specialtyRepository.save(new Specialty(null, "Sản Phụ khoa", "Khám phụ khoa và theo dõi thai kỳ"));
            Specialty rangHamMat = specialtyRepository.save(new Specialty(null, "Răng Hàm Mặt", "Khám và điều trị nha khoa tổng quát"));

            doctorRepository.saveAll(List.of(
                    new Doctor(null, "BS. Nguyễn Văn Hùng", "Bác sĩ CKI", new BigDecimal("300000"), 20, null, noiTongQuat),
                    new Doctor(null, "BS. Trần Thị Mai", "ThS Bác sĩ", new BigDecimal("350000"), 15, null, noiTongQuat),
                    new Doctor(null, "BS. Lê Thị Thu", "Bác sĩ CKI", new BigDecimal("300000"), 25, null, nhiKhoa),
                    new Doctor(null, "BS. Phạm Quang Huy", "ThS Bác sĩ", new BigDecimal("350000"), 18, null, nhiKhoa),
                    new Doctor(null, "BS. Hoàng Minh Tuấn", "Bác sĩ CKI", new BigDecimal("400000"), 12, null, daLieu),
                    new Doctor(null, "BS. Vũ Thị Lan", "Bác sĩ CKI", new BigDecimal("350000"), 10, null, daLieu),
                    new Doctor(null, "BS. Đỗ Văn Nam", "Bác sĩ CKI", new BigDecimal("350000"), 20, null, taiMuiHong),
                    new Doctor(null, "BS. Nguyễn Thị Ngọc", "Bác sĩ CKII", new BigDecimal("450000"), 15, null, sanPhuKhoa),
                    new Doctor(null, "BS. Bùi Anh Tuấn", "Bác sĩ Răng Hàm Mặt", new BigDecimal("300000"), 20, null, rangHamMat)
            ));

            patientRepository.saveAll(List.of(
                    new Patient(null, "Nguyễn Văn Nam", "0901234567", "nam@example.com", "TP.HCM"),
                    new Patient(null, "Trần Thị Lan", "0912345678", "lan@example.com", "Hà Nội")
            ));
        }
    }
}
