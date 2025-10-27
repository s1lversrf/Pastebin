package com.example.pastebin.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Paste {
    @Id
    String id;
    String s3Key;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Paste(String id, String s3Key){
        this.id = id;
        this.s3Key = s3Key;
    }

    @PrePersist
    void createdAt() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }
}
