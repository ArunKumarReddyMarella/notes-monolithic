package com.enotes.monolithic.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3ServiceImpl {

    Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
    
    private final AmazonS3 amazonS3Client;
    private final String bucketName;
    
    public S3ServiceImpl(AmazonS3 amazonS3Client, @Qualifier("s3BucketName") String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }
    
    public String uploadFile(String fileKey, MultipartFile file) throws IOException {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            amazonS3Client.putObject(new PutObjectRequest(
                    bucketName, 
                    fileKey, 
                    file.getInputStream(), 
                    metadata
            ));
            
            // Return the file URL
            return amazonS3Client.getUrl(bucketName, fileKey).toString();
        } catch (AmazonServiceException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw e;
        }
    }
    
    public void deleteFile(String fileKey) {
        try {
            fileKey = "notes/" + fileKey;
            amazonS3Client.deleteObject(bucketName, fileKey);
        } catch (AmazonServiceException e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw e;
        }
    }
    
    public byte[] downloadFile(String fileKey) {
        try {
            fileKey = "notes/" + fileKey;
            S3Object s3Object = amazonS3Client.getObject(bucketName, fileKey);
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                return IOUtils.toByteArray(inputStream);
            }
        } catch (IOException | AmazonServiceException e) {
            log.error("Error downloading file from S3: {}", e.getMessage());
            throw new RuntimeException("Error downloading file", e);
        }
    }
}