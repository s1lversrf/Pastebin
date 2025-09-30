package com.example.pastebin.repository;

import com.example.pastebin.dto.Paste;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasteRepository extends CrudRepository<Paste, String> { }
