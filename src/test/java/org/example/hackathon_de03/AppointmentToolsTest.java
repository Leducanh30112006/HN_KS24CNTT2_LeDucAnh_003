package org.example.hackathon_de03;

import org.example.hackathon_de03.model.constant.AppointmentStatus;
import org.example.hackathon_de03.model.dto.AppointmentResponse;
import org.example.hackathon_de03.model.dto.DoctorBookingItem;
import org.example.hackathon_de03.service.AppointmentService;
import org.example.hackathon_de03.tools.AppointmentTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentToolsTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentTools appointmentTools;

    @Test
    void testCreateAppointment_Success() {
        AppointmentResponse mockResponse = AppointmentResponse.builder()
                .appointmentId(1L)
                .patientName("Nguyễn Văn Nam")
                .patientPhone("0901234567")
                .appointmentDate(LocalDateTime.now())
                .status(AppointmentStatus.CONFIRMED)
                .totalAmount(new BigDecimal("300000"))
                .message("Đặt lịch khám thành công")
                .build();

        List<DoctorBookingItem> items = List.of(new DoctorBookingItem("BS. Lê Thị Thu", 1));
        when(appointmentService.bookAppointment("0901234567", "Nguyễn Văn Nam", items)).thenReturn(mockResponse);

        AppointmentResponse response = appointmentTools.createAppointment("0901234567", "Nguyễn Văn Nam", items);

        assertNotNull(response);
        assertEquals(1L, response.getAppointmentId());
        assertEquals("0901234567", response.getPatientPhone());
    }

    @Test
    void testCreateAppointment_FailureReturnsErrorMessage() {
        List<DoctorBookingItem> items = List.of(new DoctorBookingItem("BS. Lê Thị Thu", 100));
        when(appointmentService.bookAppointment("0901234567", "Nguyễn Văn Nam", items))
                .thenThrow(new IllegalArgumentException("Không đủ số lượng lượt khám"));

        AppointmentResponse response = appointmentTools.createAppointment("0901234567", "Nguyễn Văn Nam", items);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("Lỗi"));
        assertTrue(response.getMessage().contains("Không đủ số lượng lượt khám"));
    }
}
