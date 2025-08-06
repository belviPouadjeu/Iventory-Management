package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {

    FileDTO uploadFile(MultipartFile file) throws IOException;

    InputStream downloadFile(String fileName);

    void deleteFile(String fileName);

    List<String> listFiles();

    String getPreSignedUrl(String fileName, int expiryInMinutes);
}
