package com.belvinard.gestionstock.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MicroService {
    String uploadImage(MultipartFile file) throws IOException;
    String getPreSignedUrl(String objectName, Integer expiryInMinutes);
    void deleteFile(String objectName);
    InputStream downloadFile(String objectName);
    List<String> listFiles();
    boolean fileExists(String objectName);
    String getFileUrl(String objectName);
}