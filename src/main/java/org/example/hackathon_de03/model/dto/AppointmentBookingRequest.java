package org.example.hackathon_de03.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentBookingRequest {
    private String patientPhone;
    private String patientName;
    private List<DoctorBookingItem> items;
}
