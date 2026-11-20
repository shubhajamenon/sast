package com.accenture.sast.tracker;

import com.accenture.sast.tracker.controller.ReportController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportControllerTest {

    @TempDir
    Path tempDirectory;

    @Test
    void rejectsPathsOutsideReportsDirectory() throws IOException {
        Path reportsDirectory = Files.createDirectory(tempDirectory.resolve("reports"));
        Files.writeString(tempDirectory.resolve("secret.txt"), "secret");

        ResponseEntity<Resource> response =
                new ReportController(reportsDirectory.toString()).download("../secret.txt");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void downloadsFileWithinReportsDirectory() throws IOException {
        Path reportsDirectory = Files.createDirectory(tempDirectory.resolve("reports"));
        Files.writeString(reportsDirectory.resolve("report.txt"), "report contents");

        ResponseEntity<Resource> response =
                new ReportController(reportsDirectory.toString()).download("report.txt");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("report contents",
                new String(response.getBody().getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }
}
