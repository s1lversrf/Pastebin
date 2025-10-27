package com.example.pastebin.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class IdGeneratorService {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ID_LENGTH = 8;

    private final SecureRandom random = new SecureRandom();

    public String generateId() {
        StringBuilder id = new StringBuilder(ID_LENGTH);

        for (int i = 0; i < ID_LENGTH; i++) {
            int randomIndex = random.nextInt(62);
            id.append(BASE62_ALPHABET.charAt(randomIndex));
        }

        return id.toString();
    }
}
