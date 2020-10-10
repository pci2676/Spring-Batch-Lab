package com.javabom.bombatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SimpleChunkOrientedBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleChunkOrientedBatchApplication.class, args);
    }
}
