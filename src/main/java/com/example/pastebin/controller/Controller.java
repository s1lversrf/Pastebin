package com.example.pastebin.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.pastebin.dto.Paste;
import com.example.pastebin.dto.PasteResponse;
import com.example.pastebin.service.PasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pastebin")
public class Controller {
    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<PasteResponse> addPaste(@RequestBody String content){
        if(content == null || content.isBlank()){
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

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/pastebin/" + paste.getId())
                .body(pasteResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasteResponse> getPaste(@PathVariable String id){
        try {
            Paste paste = pasteService.getPaste(id);
            String content = pasteService.getPasteContent(id);
            PasteResponse pasteResponse = new PasteResponse(
                    id,
                    content,
                    paste.getCreatedAt(),
                    paste.getUpdatedAt());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(pasteResponse);
        }
        catch (NotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasteResponse> updatePaste(@PathVariable String id, @RequestBody String newContent){
        if(newContent == null || newContent.isBlank()){
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

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(pasteResponse);
        }
        catch (NotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaste(@PathVariable String id){
        try {
            pasteService.deletePaste(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }
        catch (NotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }
}
