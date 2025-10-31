package com.example.pastebin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PasteCreateRequest(
        @NotBlank(message = "Content cannot be empty")
        String content,

        @Positive(message = "Expiration must be positive")
        @Max(value = 525960, message = "Maximum expiration is 1 year")
        Integer expirationMinutes
) {}
