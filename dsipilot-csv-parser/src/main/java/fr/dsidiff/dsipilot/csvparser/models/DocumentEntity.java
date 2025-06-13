package fr.dsidiff.dsipilot.csvparser.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter @Setter
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * id generated
     */
    @Column(length = 128, unique = true, nullable = false)
    private String documentId;

    /**
     * original file path
     */
    private String filePath;
    /**
     * file name
     */
    private String fileName;
    /**
     * file mime type
     */
    private String fileType;
    /**
     * path after file is moved
     */
    private String storagePath;
    /**
     * url if exists
     */
    private String url;
    /**
     * check if files is in repository, if not exist: set to false, default value is NULL
     */
    @Column(name = "file_exists", nullable = true)
    private Boolean fileExists;


}
