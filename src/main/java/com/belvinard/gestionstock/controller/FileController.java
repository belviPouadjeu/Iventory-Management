package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.FileDTO;
import com.belvinard.gestionstock.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/files")
@RequiredArgsConstructor
@Tag(name = "File-Controller", description = "API de gestion des fichiers (via MinIO)")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    @Operation(summary = "Uploader une image (ADMIN ou MANAGER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès", content = @Content(schema = @Schema(implementation = FileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'upload", content = @Content)
    })
    public ResponseEntity<FileDTO> uploadFile(
            @Parameter(description = "Image à uploader") @RequestParam("file") MultipartFile file) {
        try {
            FileDTO result = fileService.uploadFile(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    FileDTO.builder()
                            .message("Erreur lors de l'upload : " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/download/{fileName}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Télécharger un fichier (ADMIN ou MANAGER)")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName) {
        try {
            InputStream inputStream = fileService.downloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un fichier (ADMIN)")
    public ResponseEntity<Map<String, String>> deleteFile(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName) {
        try {
            fileService.deleteFile(fileName);
            return ResponseEntity.ok(Map.of("message", "Fichier supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Lister tous les fichiers (ADMIN ou MANAGER)")
    public ResponseEntity<List<String>> listFiles() {
        try {
            return ResponseEntity.ok(fileService.listFiles());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/url/{fileName}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Obtenir l'URL pré-signée d'un fichier (ADMIN ou MANAGER)")
    public ResponseEntity<Map<String, String>> getPreSignedUrl(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName,
            @Parameter(description = "Durée d'expiration en minutes") @RequestParam(defaultValue = "60") Integer expiryInMinutes) {
        try {
            String url = fileService.getPreSignedUrl(fileName, expiryInMinutes);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
