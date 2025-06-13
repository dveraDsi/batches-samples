package fr.dsidiff.dsipilot.csvparser.batch.writer;

import fr.dsidiff.dsipilot.csvparser.data.DocumentRepository;
import fr.dsidiff.dsipilot.csvparser.models.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class CsvAndDbWriter implements ItemWriter<DocumentEntity> {
    private final DocumentRepository documentRepository;
    private final Set<String> writtenPaths = new HashSet<>();


    @Value("#{jobParameters['filePath']}")
    private String filePath;

    @Value("${output.directory}")
    private String outputDir;


    @Override
    public void write(Chunk<? extends DocumentEntity> items) throws Exception {
        // Avoid duplicate insertions to DB
        List<DocumentEntity> uniqueDocs = new ArrayList<>();

        for (DocumentEntity doc : items) {
            if (!documentRepository.existsByFilePath(doc.getFilePath()) &&
                    uniqueDocs.stream().noneMatch(d -> d.getFilePath().equals(doc.getFilePath()))) {
                uniqueDocs.add(doc);
            }
        }

        if (!uniqueDocs.isEmpty()) {
            documentRepository.saveAll(uniqueDocs);
        }

        // Write to output .txt file
        String fileName = new File(filePath).getName().replace(".csv", ".csv.txt");
        Path outputFile = Paths.get(outputDir, fileName);

        // Load already written lines from existing file (if any)
        if (Files.exists(outputFile)) {
            List<String> alreadyWritten = Files.readAllLines(outputFile);
            writtenPaths.addAll(alreadyWritten);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                outputFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            for (DocumentEntity doc : uniqueDocs) {
                if (!writtenPaths.contains(doc.getFileName())) {
                    writer.write(doc.getFileName());
                    writer.newLine();
                    writtenPaths.add(doc.getFileName());
                }
            }
        }
    }
}