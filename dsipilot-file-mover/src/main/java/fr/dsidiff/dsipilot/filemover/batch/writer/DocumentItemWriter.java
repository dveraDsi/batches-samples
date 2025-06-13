package fr.dsidiff.dsipilot.filemover.batch.writer;

import fr.dsidiff.dsipilot.filemover.batch.job.DocumentJobListener;
import fr.dsidiff.dsipilot.filemover.model.DocumentEntity;
import fr.dsidiff.dsipilot.filemover.data.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class DocumentItemWriter implements ItemWriter<DocumentEntity> {
    private final DocumentRepository documentRepository;
    private final ObjectProvider<DocumentJobListener> listenerProvider;


    @Value("#{jobParameters['reportPath']}")
    private String reportPath;

    private final List<String> errors = new ArrayList<>();
    private int successCount = 0;

    @Override
    public void write(Chunk<? extends DocumentEntity> items) {
        DocumentJobListener listener = listenerProvider.getIfAvailable();
        for (DocumentEntity doc : items) {
            try {
                documentRepository.save(doc);
                if (listener != null) {
                    listener.incrementMoved();
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.addError(doc.getFilePath() + ": " + e.getMessage());
                }
            }
        }
    }

    public void writeReport(long durationMillis) {
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-report.txt";
        File reportFile = new File(reportPath, fileName);

        try {
            if (!reportFile.getParentFile().exists()) {
                System.out.println("Folder created " + reportFile.getParentFile().mkdirs());
            }
            try (PrintWriter writer = new PrintWriter(reportFile)) {
                writer.println("document moved = " + successCount);
                writer.println("document errors = " + errors.size());
                writer.println("total duration of batch = " + durationMillis);
                writer.println("errors:");
                errors.forEach(writer::println);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write report", e);
        }
    }
}