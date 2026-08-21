package org.example.hackathon_de03.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RAGService {

    private final VectorStore vectorStore;

    public int ingestClinicPdf() {
        try {
            Resource pdfResource = new ClassPathResource("De03_MediCareClinic_ThongTin.pdf");
            if (!pdfResource.exists()) {
                return 0;
            }

            TikaDocumentReader tikaReader = new TikaDocumentReader(pdfResource);
            List<Document> rawDocuments = tikaReader.read();

            TokenTextSplitter splitter = new TokenTextSplitter(400, 100, 10, 5000, true, Collections.emptyList());
            List<Document> splitDocuments = splitter.apply(rawDocuments);

            for (Document doc : splitDocuments) {
                doc.getMetadata().put("source", "De03_MediCareClinic_ThongTin.pdf");
                doc.getMetadata().put("type", "clinic_info");
            }

            vectorStore.add(splitDocuments);
            return splitDocuments.size();
        } catch (Exception e) {
            throw new RuntimeException("Error ingesting PDF: " + e.getMessage(), e);
        }
    }

    public String searchClinicInfo(String question) {
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(question)
                    .topK(3)
                    .similarityThreshold(0.5)
                    .build();

            List<Document> results = vectorStore.similaritySearch(searchRequest);
            if (results == null || results.isEmpty()) {
                results = vectorStore.similaritySearch(
                        SearchRequest.builder().query(question).topK(3).build()
                );
            }

            if (results == null || results.isEmpty()) {
                return "Không tìm thấy thông tin phù hợp.";
            }

            return results.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n\n---\n\n"));
        } catch (Exception e) {
            return "Lỗi khi tra cứu: " + e.getMessage();
        }
    }
}
