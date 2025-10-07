package com.example.pastebin.service;

import com.example.pastebin.dto.Paste;
import com.example.pastebin.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasteService {
    private final PasteRepository repository;
    private final IdGeneratorService idGeneratorService;
    private final S3Service s3Service;

    public String AddPaste(String content){
        String id = idGeneratorService.generateId();
        if(repository.existsById(id)){
            AddPaste(content);
        }
        String s3key = s3Service.uploadPaste(content);
        repository.save(new Paste(id, s3key));

        return id;
    }
}