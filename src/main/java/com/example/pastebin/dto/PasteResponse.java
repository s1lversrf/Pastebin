package com.example.pastebin.dto;

import java.time.LocalDateTime;

public record PasteResponse(
        String id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}