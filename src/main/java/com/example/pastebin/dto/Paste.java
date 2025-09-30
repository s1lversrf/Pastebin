package com.example.pastebin.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Paste {
    @Id
    String id;
    String s3Key;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
