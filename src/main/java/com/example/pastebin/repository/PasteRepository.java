package com.example.pastebin.repository;

import com.example.pastebin.dto.Paste;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PasteRepository extends CrudRepository<Paste, String> {
    @Modifying
    @Query("DELETE FROM Paste p WHERE p.expiresAt <= :now")
    int deleteExpiredPastes(Instant now);

    @Query("SELECT p.id FROM Paste p WHERE p.expiresAt <= :now")
    List<String> findExpiredPasteIds(Instant now);
}
