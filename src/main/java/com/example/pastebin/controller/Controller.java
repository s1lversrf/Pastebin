package com.example.pastebin.controller;

import com.example.pastebin.service.PasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/pastebin")
public class Controller {
    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<String> addPaste(@RequestBody String content){
        String id = pasteService.AddPaste(content);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(id);
    }
}
