package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.service.MicroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File-Controller", description = "API de gestion des fichiers MinIO")
public class FileController {

    private final MicroService microService;

    @PostMapping("/upload")
    @Operation(summary = "Uploader un fichier")
    public ResponseEntity<Map<String, String>> uploadFile(
            @Parameter(description = "Fichier à uploader") @RequestParam("file") MultipartFile file) {
        try {
            String fileName = microService.uploadImage(file);
            return ResponseEntity.ok(Map.of(
                    "fileName", fileName,
                    "message", "Fichier uploadé avec succès",
                    "url", microService.getFileUrl(fileName)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{fileName}")
    @Operation(summary = "Télécharger un fichier")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName) {
        try {
            InputStream inputStream = microService.downloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileName}")
    @Operation(summary = "Supprimer un fichier")
    public ResponseEntity<Map<String, String>> deleteFile(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName) {
        try {
            microService.deleteFile(fileName);
            return ResponseEntity.ok(Map.of("message", "Fichier supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lister tous les fichiers")
    public ResponseEntity<List<String>> listFiles() {
        try {
            List<String> files = microService.listFiles();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/url/{fileName}")
    @Operation(summary = "Obtenir l'URL pré-signée d'un fichier")
    public ResponseEntity<Map<String, String>> getPreSignedUrl(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName,
            @Parameter(description = "Durée d'expiration en minutes") @RequestParam(defaultValue = "60") Integer expiryInMinutes) {
        try {
            String url = microService.getPreSignedUrl(fileName, expiryInMinutes);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de la génération de l'URL: " + e.getMessage()));
        }
    }
}