package org.example.hackathon_de03.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDetailDto {
    private String doctorName;
    private String specialty;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
