package fr.dsidiff.dsipilot.filemover.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, unique = true, nullable = false)
    private String documentId;

    private String filePath;
    private String fileName;
    private String fileType;
    private String storagePath;
    private String url;

    @Column(name = "file_exists", nullable = true)
    private Boolean fileExists;
}

