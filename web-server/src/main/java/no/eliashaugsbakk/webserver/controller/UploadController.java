package no.eliashaugsbakk.webserver.controller;

import no.eliashaugsbakk.webserver.service.auth.AuthService;
import no.eliashaugsbakk.webserver.service.ingestion.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;


@RestController
@RequestMapping("/api/v1")
public class UploadController {

  private final IngestionService ingestionService;
  private final AuthService authService;

  public UploadController(IngestionService ingestionService, AuthService authService) {
    this.ingestionService = ingestionService;
    this.authService = authService;
  }

  @PostMapping("/upload")
  public ResponseEntity<String> handleUpload(
      @RequestHeader("Authorization") String token,
      @RequestParam("file") MultipartFile file,
      @RequestParam("sha256") String clientHash) {

    // Authentification of the Auth-Token
    if (!authService.isValid(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("FAILURE: Unauthorized access.");
    }

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("FAILURE: File is empty.");
    }

    try {
      byte[] fileBytes = file.getBytes();
      String calculatedHash = calculateSHA256(fileBytes);

      if (!calculatedHash.equalsIgnoreCase(clientHash)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("FAILURE: Integrity check failed (Hash mismatch).");
      }

      ingestionService.ingestBundle(fileBytes, file.getOriginalFilename());

      return ResponseEntity.ok("SUCCESS: Content ingested and live.");

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("FAILURE: " + e.getMessage());
    }
  }

  private String calculateSHA256(byte[] data) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedHash = digest.digest(data);
    return HexFormat.of().formatHex(encodedHash);
  }
}

