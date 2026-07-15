package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service.account}")
    private Resource serviceAccount;

    @PostConstruct
    public void initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(
                                serviceAccount.getInputStream()))
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase successfully initialized.");
            } catch (Exception e) {
                System.err.println("WARNING: Failed to initialize Firebase: " + e.getMessage() + ". Running in local development mode without Firebase.");
            }
        }
    }
}