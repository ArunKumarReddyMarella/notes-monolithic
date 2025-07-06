package com.enotes.monolithic.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
    
    @Value("${cloud.aws.access-key}")
    private String accessKey;
    
    @Value("${cloud.aws.secret-key}")
    private String secretKey;
    
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${cloud.aws.s3.region:us-east-1}")
    private String region;
    
    @Bean
    public AmazonS3 amazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
    
    @Bean
    public String s3BucketName() {
        return bucketName;
    }
}