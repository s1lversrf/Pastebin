package com.example.pastebin.dto;

import java.time.Instant;

public record PasteResponse(
        String id,
        String content,
        Instant createdAt,
        Instant updatedAt,
        Instant expiresAt
) {}