package com.example.pastebin.controller;

import com.amazonaws.services.kms.model.NotFoundException;
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
    public ResponseEntity<String> addPaste(@RequestBody String content){
        if(content == null || content.isBlank()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        String id = pasteService.addPaste(content);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/pastebin/" + id)
                .body(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getPaste(@PathVariable String id){
        try {
            String content = pasteService.getPasteContent(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(content);
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
    public ResponseEntity<String> updatePaste(@PathVariable String id, @RequestBody String newContent){
        if(newContent == null || newContent.isBlank()){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        try {
            String content = pasteService.updatePaste(id, newContent);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(content);
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
