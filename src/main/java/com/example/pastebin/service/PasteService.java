package com.example.pastebin.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.pastebin.dto.Paste;
import com.example.pastebin.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasteService {
    private final PasteRepository repository;
    private final IdGeneratorService idGeneratorService;
    private final S3Service s3Service;

    public Paste addPaste(String content){
        String id = idGeneratorService.generateId();
        if(repository.existsById(id)){
            addPaste(content);
        }
        String s3key = s3Service.uploadPaste(content);
        Paste paste = new Paste(id, s3key);
        repository.save(paste);

        return paste;
    }

    public Paste getPaste(String id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paste not found"));
    }

    public String getPasteContent(String id) throws RuntimeException {
        Paste paste = getPaste(id);

        return s3Service.getPaste(paste.getS3Key());
    }

    public Paste updatePaste(String id, String content) throws NotFoundException {
        Paste paste = getPaste(id);

        s3Service.updatePaste(paste.getS3Key(), content);
        paste.setUpdatedAt(LocalDateTime.now());
        repository.save(paste);

        return paste;
    }

    public void deletePaste(String id) throws NotFoundException {
        Paste paste = repository.findById(id).orElse(null);
        if(paste == null){
            throw new NotFoundException("Paste not found");
        }

        String s3Key = paste.getS3Key();

        s3Service.deletePaste(s3Key);
        repository.deleteById(id);
    }

}