package dejay.rnd.billyG.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * File Writer 에서 경로 생성 시 사용된다.
 *
 */
@Slf4j
@Configuration
@Data
public class ImageProperties {


    @Value("${file.upload.location.mac}")
    private String defaultPath;

    @PostConstruct
    private void init() {
        log.info("path:: {}",this.defaultPath);

        String os = System.getProperty("os.name").toLowerCase();
        log.info("os:: {}", os);
    }

}
