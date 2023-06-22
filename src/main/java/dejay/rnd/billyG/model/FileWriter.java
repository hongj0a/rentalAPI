package dejay.rnd.billyG.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.s3.AmazonS3;
import dejay.rnd.billyG.config.ImageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileWriter {

    private final ImageProperties imageProperties;


    public long writeFile( MultipartFile multipartFile, String filePath ) {
        try {
            multipartFile.transferTo(new File(filePath));
        } catch (IllegalStateException ile) {
            throw new RuntimeException("file write error");
        } catch ( IOException ioe) {
            log.info("ioe", ioe);
            throw new RuntimeException("ioe error");
        }
        return multipartFile.getSize();
    }

    public String getFilePath(String fileId, MultipartFile sourceFile) {

        return imageProperties.getDefaultPath() +"/" + fileId;
    }

    private static String getMimeType(String filePath) {
        return FilenameUtils.getExtension(filePath);
    }

    public static String dateStr() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return now.format(dateTimeFormatter);
    }
}
