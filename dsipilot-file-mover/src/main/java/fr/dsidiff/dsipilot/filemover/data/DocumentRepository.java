package fr.dsidiff.dsipilot.filemover.repositories;

import fr.dsidiff.dsipilot.filemover.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    @Query("SELECT d FROM DocumentEntity d WHERE d.fileExists = true AND d.storagePath IS NULL AND d.url IS NULL")
    List<DocumentEntity> findPendingDocuments();
}