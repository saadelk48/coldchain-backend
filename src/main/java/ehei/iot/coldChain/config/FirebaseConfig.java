package ehei.iot.coldChain.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ClassPathResource("firebase-service-account.json").getInputStream()
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("✅ Firebase initialized");

        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to initialize Firebase", e);
        }
    }
}
