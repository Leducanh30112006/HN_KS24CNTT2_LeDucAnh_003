package org.example.hackathon_de03;

import org.example.hackathon_de03.model.constant.AppointmentStatus;
import org.example.hackathon_de03.model.dto.AppointmentResponse;
import org.example.hackathon_de03.model.dto.DoctorBookingItem;
import org.example.hackathon_de03.model.entity.Appointment;
import org.example.hackathon_de03.model.entity.AppointmentDetail;
import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Patient;
import org.example.hackathon_de03.model.entity.Specialty;
import org.example.hackathon_de03.repository.AppointmentDetailRepository;
import org.example.hackathon_de03.repository.AppointmentRepository;
import org.example.hackathon_de03.repository.DoctorRepository;
import org.example.hackathon_de03.repository.PatientRepository;
import org.example.hackathon_de03.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentDetailRepository appointmentDetailRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Specialty specialty;
    private Doctor doctorA;
    private Doctor doctorB;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1L, "Nhi khoa", "Chuyên khoa nhi");
        doctorA = new Doctor(1L, "BS. Lê Thị Thu", "Bác sĩ CKI", new BigDecimal("300000"), 10, null, specialty);
        doctorB = new Doctor(2L, "BS. Phạm Quang Huy", "ThS Nhi khoa", new BigDecimal("350000"), 2, null, specialty);
    }

    @Test
    void testBookAppointment_Success() {
        when(doctorRepository.findByNameIgnoreCase("BS. Lê Thị Thu")).thenReturn(Optional.of(doctorA));
        when(patientRepository.findByPhone("0901234567")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> {
            Patient p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(200L);
            return a;
        });
        when(appointmentDetailRepository.save(any(AppointmentDetail.class))).thenAnswer(inv -> {
            AppointmentDetail ad = inv.getArgument(0);
            ad.setId(300L);
            return ad;
        });

        List<DoctorBookingItem> items = List.of(new DoctorBookingItem("BS. Lê Thị Thu", 2));

        AppointmentResponse response = appointmentService.bookAppointment("0901234567", "Nguyễn Văn Nam", items);

        assertNotNull(response);
        assertEquals(200L, response.getAppointmentId());
        assertEquals("Nguyễn Văn Nam", response.getPatientName());
        assertEquals("0901234567", response.getPatientPhone());
        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
        assertEquals(new BigDecimal("600000"), response.getTotalAmount());
        assertEquals(8, doctorA.getStock());
        verify(doctorRepository, times(1)).save(doctorA);
        verify(appointmentRepository, atLeastOnce()).save(any(Appointment.class));
    }

    @Test
    void testBookAppointment_InsufficientStock_ThrowsException() {
        when(doctorRepository.findByNameIgnoreCase("BS. Phạm Quang Huy")).thenReturn(Optional.of(doctorB));

        List<DoctorBookingItem> items = List.of(new DoctorBookingItem("BS. Phạm Quang Huy", 5));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.bookAppointment("0901234567", "Nguyễn Văn Nam", items)
        );

        assertTrue(exception.getMessage().contains("không đủ số lượng"));
        assertEquals(2, doctorB.getStock());
        verify(appointmentRepository, never()).save(any());
        verify(appointmentDetailRepository, never()).save(any());
    }

    @Test
    void testBookAppointment_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findByNameIgnoreCase("BS. Không Tồn Tại")).thenReturn(Optional.empty());
        when(doctorRepository.searchDoctorByName("BS. Không Tồn Tại")).thenReturn(List.of());
        when(doctorRepository.findByNameContainingIgnoreCase("BS. Không Tồn Tại")).thenReturn(List.of());

        List<DoctorBookingItem> items = List.of(new DoctorBookingItem("BS. Không Tồn Tại", 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                appointmentService.bookAppointment("0901234567", "Nguyễn Văn Nam", items)
        );

        assertTrue(exception.getMessage().contains("Không tìm thấy bác sĩ"));
        verify(appointmentRepository, never()).save(any());
    }
}
