package fr.dsidiff.dsipilot.filemover;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RequiredArgsConstructor
public class DsipilotFileMoveApplication
        implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job moveDocumentJob;


    public static void main(String[] args) {
        SpringApplication.run(DsipilotFileMoveApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Example parameters - customize as needed
        // storagePath=D:/tomcat/webapps/storage-path  baseUrl=http://localhost:8080/storage-path reportPath=D:/DSIPILOT_V9/TESTWRITER/IN/reports
        Map<String, String> paramMap = parseArgs(args);

        String storagePath = paramMap.getOrDefault("storagePath", "/path/to/storage");
        String baseUrl = paramMap.getOrDefault("baseUrl", "http://localhost/files");
        String reportPath = paramMap.getOrDefault("reportPath", "./batch-reports");

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("storagePath", storagePath)
                .addString("baseUrl", baseUrl)
                .addString("reportPath", reportPath)
                .addString("run.id", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .toJobParameters();

        jobLauncher.run(moveDocumentJob, jobParameters);
    }

    private Map<String, String> parseArgs(String[] args) {
        Map<String, String> paramMap = new HashMap<>();
        for (String arg : args) {
            if (arg.contains("=")) {
                String[] split = arg.split("=", 2);
                paramMap.put(split[0], split[1]);
            }
        }
        return paramMap;
    }
}