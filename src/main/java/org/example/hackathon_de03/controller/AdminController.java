package org.example.hackathon_de03.controller;

import lombok.RequiredArgsConstructor;
import org.example.hackathon_de03.service.RAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RAGService ragService;

    @PostMapping("/ingest-store-info")
    public ResponseEntity<Map<String, Object>> ingestClinicInfo() {
        int chunks = ragService.ingestClinicPdf();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Nạp dữ liệu thành công",
                "chunksIngested", chunks
        ));
    }
}
