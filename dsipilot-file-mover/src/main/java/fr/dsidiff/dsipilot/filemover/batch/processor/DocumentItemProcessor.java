package fr.dsidiff.dsipilot.filemover.batch.processor;

import fr.dsidiff.dsipilot.filemover.model.DocumentEntity;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
// Because Spring Boot tries to instantiate the bean during application startup (before job execution), jobParameters doesn’t exist yet.
@StepScope
public class DocumentItemProcessor implements
        ItemProcessor<DocumentEntity, DocumentEntity> {

    @Value("#{jobParameters['storagePath']}")
    private String storagePath;

    @Value("#{jobParameters['baseUrl']}")
    private String baseUrl;

    @Override
    public DocumentEntity process(DocumentEntity document) throws Exception {
        String originalPath = document.getFilePath();
        File originalFile = new File(originalPath);

        if (!originalFile.exists()) {
            throw new FileNotFoundException("File not found: " + originalPath);
        }

        String newFileName = String.format("%s-%s-%s",
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                document.getDocumentId(),
                originalFile.getName());

        File targetFile = new File(storagePath, newFileName);

        Files.createDirectories(targetFile.getParentFile().toPath()); // ensure parent exists
        // on peut eventuellement passer un param pour move / copy
        Files.move(originalFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        document.setStoragePath(targetFile.getAbsolutePath());
        document.setUrl(baseUrl + "/" + newFileName);

        return document;
    }
}

