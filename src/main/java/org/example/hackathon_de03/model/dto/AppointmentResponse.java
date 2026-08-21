package org.example.hackathon_de03.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hackathon_de03.model.constant.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long appointmentId;
    private String patientName;
    private String patientPhone;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private BigDecimal totalAmount;
    private String note;
    private List<AppointmentDetailDto> details;
    private String message;
}
