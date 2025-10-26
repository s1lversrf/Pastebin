package com.example.pastebin.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.pastebin.dto.Paste;
import com.example.pastebin.dto.PasteResponse;
import com.example.pastebin.service.PasteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pastebin")
public class Controller {
    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<PasteResponse> addPaste(@RequestBody String content){
        if(content == null || content.isBlank()){
            logBadRequest();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Paste paste = pasteService.addPaste(content);
        PasteResponse pasteResponse = new PasteResponse(
                paste.getId(),
                content,
                paste.getCreatedAt(),
                paste.getUpdatedAt());

        log.info("Paste id={} successfully created", paste.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", String.format("/pastebin/%s", paste.getId()))
                .body(pasteResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasteResponse> getPaste(@PathVariable String id){
        try {
            Paste paste = pasteService.getPasteMetadata(id);
            String content = pasteService.getPasteContent(id);

            PasteResponse pasteResponse = new PasteResponse(
                    id,
                    content,
                    paste.getCreatedAt(),
                    paste.getUpdatedAt());

            log.info("Paste id={} successfully retrieved", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(pasteResponse);
        }
        catch (NotFoundException e){
            logNotFound(id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (RuntimeException e) {
            logUnexpectedError(id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasteResponse> updatePaste(@PathVariable String id, @RequestBody String newContent){
        if(newContent == null || newContent.isBlank()){
            logBadRequest();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        try {
            Paste paste = pasteService.updatePaste(id, newContent);
            PasteResponse pasteResponse = new PasteResponse(
                    paste.getId(),
                    newContent,
                    paste.getCreatedAt(),
                    paste.getUpdatedAt());

            log.info("Paste id={} successfully updated", id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(pasteResponse);
        }
        catch (NotFoundException e){
            logNotFound(id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (RuntimeException e) {
            logUnexpectedError(id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaste(@PathVariable String id){
        try {
            pasteService.deletePaste(id);
            log.info("Paste id={} successfully deleted", id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
        catch (NotFoundException e){
            logNotFound(id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    private void logNotFound(String id) {
        log.warn("Paste not found for id={}", id);
    }

    private void logBadRequest() {
        log.warn("Paste content is null or empty");
    }

    private void logUnexpectedError(String id, Exception e) {
        log.error("Unexpected error while processing paste id={}", id, e);
    }
}