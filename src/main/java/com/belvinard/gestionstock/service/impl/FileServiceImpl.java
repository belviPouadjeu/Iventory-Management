package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.FileDTO;
import com.belvinard.gestionstock.models.File;
import com.belvinard.gestionstock.repositories.FileRepository;
import com.belvinard.gestionstock.service.FileService;
import com.belvinard.gestionstock.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioService minioService;
    private final FileRepository fileRepository;

    @Override
    public FileDTO uploadFile(MultipartFile file) throws IOException {
        String fileName = minioService.uploadImage(file);
        String url = minioService.getFileUrl(fileName);

        File fileEntity = File.builder()
                .fileName(fileName)
                .url(url)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        File savedFile = fileRepository.save(fileEntity);

        return FileDTO.builder()
                .id(savedFile.getId())
                .fileName(savedFile.getFileName())
                .url(savedFile.getUrl())
                .contentType(savedFile.getContentType())
                .size(savedFile.getSize())
                .message("Fichier uploadé avec succès")
                .build();
    }

    @Override
    public InputStream downloadFile(String fileName) {
        return minioService.downloadFile(fileName);
    }

    @Override
    public void deleteFile(String fileName) {
        minioService.deleteFile(fileName);
        File file = fileRepository.findByFileName(fileName);
        if (file != null) fileRepository.delete(file);
    }

    @Override
    public List<String> listFiles() {
        return minioService.listFiles();
    }

    @Override
    public String getPreSignedUrl(String fileName, int expiryInMinutes) {
        return minioService.getPreSignedUrl(fileName, expiryInMinutes);
    }
}
