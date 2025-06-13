package fr.dsidiff.dsipilot.csvparser.data;

import fr.dsidiff.dsipilot.csvparser.models.DocumentEntity;
import fr.dsidiff.dsipilot.csvparser.models.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository
        extends JpaRepository<LogEntity, Long> {
}
