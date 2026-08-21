package org.example.hackathon_de03.tools;

import lombok.RequiredArgsConstructor;
import org.example.hackathon_de03.model.dto.DoctorDto;
import org.example.hackathon_de03.model.entity.Doctor;
import org.example.hackathon_de03.model.entity.Specialty;
import org.example.hackathon_de03.repository.DoctorRepository;
import org.example.hackathon_de03.repository.SpecialtyRepository;
import org.example.hackathon_de03.service.RAGService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClinicTools {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final RAGService ragService;

    @Tool(description = "Tìm kiếm bác sĩ theo tên hoặc từ khoá, trả về tên, giá, số lượt khám còn trống, chuyên khoa")
    public List<DoctorDto> searchDoctorByName(
            @ToolParam(description = "Tên bác sĩ hoặc từ khóa cần tìm") String keyword
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return doctorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        }

        String cleanKeyword = keyword.trim();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(cleanKeyword);
        if (doctors.isEmpty()) {
            doctors = doctorRepository.searchDoctorByName(cleanKeyword);
        }

        return doctors.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Tool(description = "Trả về danh sách bác sĩ thuộc 1 chuyên khoa, mỗi bác sĩ kèm đầy đủ giá và số lượt khám còn trống")
    public List<DoctorDto> searchDoctorBySpecialty(
            @ToolParam(description = "Tên chuyên khoa") String specialtyName
    ) {
        if (specialtyName == null || specialtyName.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String cleanName = specialtyName.trim();
        List<Doctor> doctors = doctorRepository.searchDoctorBySpecialtyName(cleanName);

        if (doctors.isEmpty()) {
            Specialty specialty = specialtyRepository.findByNameIgnoreCase(cleanName)
                    .orElseGet(() -> specialtyRepository.findByName(cleanName));
            if (specialty != null) {
                doctors = doctorRepository.searchDoctorBySpecialty(specialty);
            }
        }

        return doctors.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Tool(description = "Nhận câu hỏi về phòng khám (địa chỉ, giờ hoạt động, chính sách...) và trả về đoạn nội dung liên quan nhất từ vector store")
    public String getClinicInfo(
            @ToolParam(description = "Câu hỏi của khách về phòng khám") String question
    ) {
        return ragService.searchClinicInfo(question);
    }

    private DoctorDto toDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .description(doctor.getDescription())
                .price(doctor.getPrice())
                .availableSlots(doctor.getStock())
                .specialty(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "")
                .build();
    }
}
