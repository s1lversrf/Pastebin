package com.example.pastebin.dto;

import jakarta.validation.constraints.NotBlank;

public record PasteUpdateRequest(
        @NotBlank(message = "Content cannot be empty")
        String content
) {}
