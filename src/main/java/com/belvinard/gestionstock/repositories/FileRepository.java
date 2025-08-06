package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByFileName(String fileName);
}