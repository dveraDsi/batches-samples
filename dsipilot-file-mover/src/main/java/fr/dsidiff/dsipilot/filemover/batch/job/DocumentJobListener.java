package fr.dsidiff.dsipilot.filemover.batch.job;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Getter @Setter
public class DocumentJobListener implements JobExecutionListener {
    private long startTime;
    private long movedCount = 0;
    private final List<String> errors = new ArrayList<>();

    @Value("#{jobParameters['reportPath']}")
    private String reportPath;

    public void incrementMoved() {
        movedCount++;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public DocumentJobListener withReportPath(String reportPath) {
        this.reportPath = reportPath;
        return this;
    }

    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void afterJob(@NonNull JobExecution jobExecution) {
        try {
            long duration = System.currentTimeMillis() - startTime;
            String reportFileName = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + "-report.txt";
            Path reportFile = Paths.get(reportPath, reportFileName);

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile.toFile())))) {
                writer.println("document moved = " + movedCount);
                writer.println("document errors = " + errors.size());
                writer.println("total duration of batch = " + duration);
                writer.println("errors:");
                errors.forEach(writer::println);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
