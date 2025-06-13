package fr.dsidiff.dsipilot.filemover.batch.job;

import fr.dsidiff.dsipilot.filemover.batch.processor.DocumentItemProcessor;
import fr.dsidiff.dsipilot.filemover.batch.writer.DocumentItemWriter;
import fr.dsidiff.dsipilot.filemover.model.DocumentEntity;
import fr.dsidiff.dsipilot.filemover.data.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Iterator;

@Configuration
@RequiredArgsConstructor
public class DocumentJobConfig {
    private final ObjectProvider<DocumentJobListener> listenerProvider;
    private final DocumentRepository repository;
    private final DocumentJobListener jobListener;
    private final DocumentRepository documentRepository;

    @Bean
    public Job moveDocumentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("moveDocumentJob", jobRepository)
                .start(moveDocumentStep(jobRepository, transactionManager))
                .listener(jobListener)  // <-- add your listener here
                .build();
    }

    @Bean
    public Step moveDocumentStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager) {
        Iterator<DocumentEntity> source = documentRepository
                .findAllByFileExistsTrueAndStoragePathIsNullAndUrlIsNull().iterator();

        return new StepBuilder("moveDocumentStep", jobRepository)
                .<DocumentEntity, DocumentEntity>chunk(10, transactionManager)
                .reader(new IteratorItemReader<>(source))
                .processor(processor()) // call bean method
                .writer(writer())       // call bean method
                .build();
    }

    @Bean
    @StepScope
    public DocumentItemProcessor processor() {
        return new DocumentItemProcessor(); // jobParameters are injected via @Value
    }

    @Bean
    @StepScope
    public DocumentItemWriter writer() {
        return new DocumentItemWriter(repository, listenerProvider);
    }
}