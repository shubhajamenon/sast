package com.accenture.sast.tracker.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final Path reportDirectory;

    public ReportController(@Value("${app.reports.directory:reports}") String reportDirectory) {
        this.reportDirectory = Path.of(reportDirectory).toAbsolutePath().normalize();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String filename) throws IOException {
        Path requestedPath = reportDirectory.resolve(filename).normalize();
        if (!requestedPath.startsWith(reportDirectory)) {
            return ResponseEntity.badRequest().build();
        }

        if (!Files.isDirectory(reportDirectory) || !Files.exists(requestedPath)) {
            return ResponseEntity.notFound().build();
        }

        Path realReportDirectory = reportDirectory.toRealPath();
        Path realFile = requestedPath.toRealPath();
        if (!realFile.startsWith(realReportDirectory) || !Files.isRegularFile(realFile)) {
            return ResponseEntity.notFound().build();
        }

        byte[] content = Files.readAllBytes(realFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(realFile.getFileName().toString())
                                .build()
                                .toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(content));
    }
}
