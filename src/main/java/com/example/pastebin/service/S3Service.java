package com.example.pastebin.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public String getPaste(String key) {
        try(S3ObjectInputStream inputStream = amazonS3.getObject(bucketName, key).getObjectContent()){
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read paste from S3 with key: " + key);
        }
    }

    public void updatePaste(String key, String content) throws RuntimeException {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/plain");

        amazonS3.putObject(bucketName, key, inputStream, objectMetadata);
    }

    public void deletePaste(String key) {
        amazonS3.deleteObject(bucketName, key);
    }
}
