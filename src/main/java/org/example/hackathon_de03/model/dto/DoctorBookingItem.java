package org.example.hackathon_de03.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorBookingItem {
    private String doctorName;
    private Integer quantity;
}
