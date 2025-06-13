package fr.dsidiff.dsipilot.filemover.data;

import fr.dsidiff.dsipilot.filemover.model.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findAllByFileExistsTrueAndStoragePathIsNullAndUrlIsNull();

}