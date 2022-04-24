package com.backend.apiserver.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseInitialize {
    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void initialize() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:serviceAccount.json");
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://testchat-36274.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }
}