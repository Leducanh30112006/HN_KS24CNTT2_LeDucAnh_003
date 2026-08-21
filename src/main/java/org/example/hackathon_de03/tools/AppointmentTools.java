package org.example.hackathon_de03.tools;

import lombok.RequiredArgsConstructor;
import org.example.hackathon_de03.model.dto.AppointmentResponse;
import org.example.hackathon_de03.model.dto.DoctorBookingItem;
import org.example.hackathon_de03.service.AppointmentService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentTools {

    private final AppointmentService appointmentService;

    @Tool(description = "Tự động đặt lịch khám bệnh cho bệnh nhân")
    public AppointmentResponse createAppointment(
            @ToolParam(description = "Số điện thoại bệnh nhân") String patientPhone,
            @ToolParam(description = "Họ tên bệnh nhân") String patientName,
            @ToolParam(description = "Danh sách doctorName và quantity") List<DoctorBookingItem> items
    ) {
        try {
            return appointmentService.bookAppointment(patientPhone, patientName, items);
        } catch (Exception e) {
            return AppointmentResponse.builder()
                    .message("Lỗi: " + e.getMessage())
                    .build();
        }
    }
}
