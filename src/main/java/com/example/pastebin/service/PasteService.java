package com.example.pastebin.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.pastebin.dto.Paste;
import com.example.pastebin.dto.PasteCreateRequest;
import com.example.pastebin.repository.PasteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasteService {
    private final PasteRepository repository;
    private final IdGeneratorService idGeneratorService;
    private final PasteCacheService pasteCacheService;
    private final S3Service s3Service;

    public Paste addPaste(PasteCreateRequest request) {
        String id = idGeneratorService.generateId();
        if (repository.existsById(id)){
            return addPaste(request);
        }

        Instant expiresAt = ZonedDateTime.now()
                .plusMinutes(request.expirationMinutes())
                .toInstant();

        String s3key = s3Service.uploadPaste(request.content());
        Paste paste = new Paste(id, s3key, expiresAt);

        return repository.save(paste);
    }

    public Paste getPasteFromDb(String id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paste not found"));
    }

    public Paste getPasteMetadata(String id) throws NotFoundException {
        Paste paste = pasteCacheService.getCachedPasteMetadata(id);
        if (paste != null) return paste;

        paste = getPasteFromDb(id);

        pasteCacheService.cachePasteMetadata(paste);
        return paste;
    }

    public String getPasteContent(String id) throws RuntimeException {
        long accessCount = pasteCacheService.incrementAccessCount(id);

        String content = pasteCacheService.getCachedPasteContent(id);
        if (content != null) return content;

        Paste pasteMetadata = getPasteMetadata(id);

        content = s3Service.getPaste(pasteMetadata.getS3Key());

        if (accessCount > 10) pasteCacheService.cachePasteContent(id, content);

        return content;
    }

    public Paste updatePaste(String id, String content) throws NotFoundException {
        Paste paste = getPasteFromDb(id);

        s3Service.updatePaste(paste.getS3Key(), content);
        paste.setUpdatedAt(Instant.now());
        repository.save(paste);

        pasteCacheService.evictCachedPaste(id);

        return paste;
    }

    public void deletePaste(String id) throws NotFoundException {
        Paste paste = getPasteFromDb(id);

        String s3Key = paste.getS3Key();

        s3Service.deletePaste(s3Key);
        repository.deleteById(id);
        pasteCacheService.evictCachedPaste(id);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredPastesOptimized() {
        log.info("Starting cleanup of expired pastes");

        List<String> expiredPastesIds = repository.findExpiredPasteIds(Instant.now());

        for (String pasteId : expiredPastesIds) {
            pasteCacheService.evictCachedPaste(pasteId);
        }
        int deletedCount = repository.deleteExpiredPastes(Instant.now());

        if (deletedCount > 0) {
            log.info("Cleaned up {} expired pastes", deletedCount);
        }
    }
}