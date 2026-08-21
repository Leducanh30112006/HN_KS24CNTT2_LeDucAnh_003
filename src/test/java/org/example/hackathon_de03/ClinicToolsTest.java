package org.example.hackathon_de03;

import org.example.hackathon_de03.model.dto.DoctorDto;
import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Specialty;
import org.example.hackathon_de03.repository.DoctorRepository;
import org.example.hackathon_de03.repository.SpecialtyRepository;
import org.example.hackathon_de03.service.RAGService;
import org.example.hackathon_de03.tools.ClinicTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicToolsTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private RAGService ragService;

    @InjectMocks
    private ClinicTools clinicTools;

    private Specialty nhiKhoa;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        nhiKhoa = new Specialty(1L, "Nhi khoa", "Chuyên khoa Nhi");
        doctor = new Doctor(1L, "BS. Lê Thị Thu", "Bác sĩ CKI", new BigDecimal("300000"), 25, null, nhiKhoa);
    }

    @Test
    void testSearchDoctorByName() {
        when(doctorRepository.findByNameContainingIgnoreCase("Thu")).thenReturn(List.of(doctor));

        List<DoctorDto> results = clinicTools.searchDoctorByName("Thu");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("BS. Lê Thị Thu", results.get(0).getName());
        assertEquals(25, results.get(0).getAvailableSlots());
        assertEquals("Nhi khoa", results.get(0).getSpecialty());
    }

    @Test
    void testSearchDoctorBySpecialty() {
        when(doctorRepository.searchDoctorBySpecialtyName("Nhi khoa")).thenReturn(List.of(doctor));

        List<DoctorDto> results = clinicTools.searchDoctorBySpecialty("Nhi khoa");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("BS. Lê Thị Thu", results.get(0).getName());
    }

    @Test
    void testGetClinicInfo() {
        String mockAnswer = "Phòng khám MediCare Clinic mở cửa từ 07:30 - 19:00 Thứ 2 - Thứ 7, Chủ Nhật từ 07:30 - 12:00.";
        when(ragService.searchClinicInfo("Giờ làm việc")).thenReturn(mockAnswer);

        String result = clinicTools.getClinicInfo("Giờ làm việc");

        assertNotNull(result);
        assertEquals(mockAnswer, result);
    }
}
