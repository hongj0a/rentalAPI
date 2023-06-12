package dejay.rnd.billyG.config;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FirebaseConfig {

    @Value("${firebase.sdk.path}")
    private String firebaseSettingFile;

    @PostConstruct
    public void initFirebase() {
        try {
            //Fireabse Admin SDK 초기화
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("firebase/firebaseKey.json").getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
