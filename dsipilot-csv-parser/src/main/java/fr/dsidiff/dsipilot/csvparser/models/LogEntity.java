package fr.dsidiff.dsipilot.csvparser.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter @Setter
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, unique = true, nullable = false)
    private String logId;

    private String signatureName;
    private String declaringTypeName;
    private String type;
    private String message;
    private Long duration;
    private LocalDateTime createdAt;
}
