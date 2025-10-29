package com.example.pastebin.controller;

import com.example.pastebin.dto.Paste;
import com.example.pastebin.dto.PasteCreateRequest;
import com.example.pastebin.dto.PasteResponse;
import com.example.pastebin.dto.PasteUpdateRequest;
import com.example.pastebin.service.PasteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pastebin")
public class Controller {
    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<PasteResponse> addPaste(@Valid @RequestBody PasteCreateRequest request){
        Paste paste = pasteService.addPaste(request);

        Instant expiresAt = ZonedDateTime.now()
                .plusMinutes(request.expirationMinutes())
                .toInstant();

        PasteResponse pasteResponse = new PasteResponse(
                paste.getId(),
                request.content(),
                paste.getCreatedAt(),
                paste.getUpdatedAt(),
                expiresAt);

        log.info("Paste id={} successfully created", paste.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", String.format("/pastebin/%s", paste.getId()))
                .body(pasteResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasteResponse> getPaste(@PathVariable String id){
        Paste paste = pasteService.getPasteMetadata(id);
        String content = pasteService.getPasteContent(id);

        PasteResponse pasteResponse = new PasteResponse(
                id,
                content,
                paste.getCreatedAt(),
                paste.getUpdatedAt(),
                paste.getExpiresAt());

        log.info("Paste id={} successfully retrieved", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pasteResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasteResponse> updatePaste(
            @PathVariable String id,
            @Valid @RequestBody PasteUpdateRequest request){
        Paste paste = pasteService.updatePaste(id, request.content());
        PasteResponse pasteResponse = new PasteResponse(
                paste.getId(),
                request.content(),
                paste.getCreatedAt(),
                paste.getUpdatedAt(),
                paste.getExpiresAt());

        log.info("Paste id={} successfully updated", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pasteResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaste(@PathVariable String id){
        pasteService.deletePaste(id);

        log.info("Paste id={} successfully deleted", id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}