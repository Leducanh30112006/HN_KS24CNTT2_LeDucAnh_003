package org.example.hackathon_de03;

import org.example.hackathon_de03.service.DatabaseInitializeService;
import org.example.hackathon_de03.service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class HackathonDe03Application implements CommandLineRunner {

    @Autowired
    private DatabaseInitializeService databaseInitializeService;

    @Autowired
    private RAGService ragService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(HackathonDe03Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        databaseInitializeService.initializeDatabase();

        try {
            Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM vector_store", Integer.class);
            if (count != null && count == 0) {
                ragService.ingestClinicPdf();
            }
        } catch (Exception e) {
            try {
                ragService.ingestClinicPdf();
            } catch (Exception ignored) {
            }
        }
    }
}
