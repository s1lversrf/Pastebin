package com.example.pastebin.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Paste {
    @Id
    String id;
    String s3Key;
    Instant createdAt;
    Instant updatedAt;
    Instant expiresAt;

    public Paste(String id, String s3Key, Instant expiresAt) {
        this.id = id;
        this.s3Key = s3Key;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    void createdAt() {
        this.createdAt = this.updatedAt = Instant.now();
    }
}
