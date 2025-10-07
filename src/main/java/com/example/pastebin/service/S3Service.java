package com.example.pastebin.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadPaste(String content) {
        UUID uuid = UUID.randomUUID();

        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/plain");

        amazonS3.putObject(bucketName, uuid.toString(), inputStream, objectMetadata);

        return uuid.toString();
    }
}
