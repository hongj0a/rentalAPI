package dejay.rnd.billyG.service;

import dejay.rnd.billyG.config.ImageProperties;
import dejay.rnd.billyG.model.FileWriter;
import dejay.rnd.billyG.model.ImageFile;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.except.AppException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
@Service
public class FileUploadService {
    private FileWriter fileWriter;
    private final Path fileStorageLocation;

    @Autowired
    FileUploadService(ImageProperties imageProperties, FileWriter fileWriter) {
        this.fileStorageLocation = Paths.get(imageProperties.getDefaultPath())
                .toAbsolutePath().normalize();
        this.fileWriter = fileWriter;
    }

    public ImageFile upload(MultipartFile sourceFile ) {

        String fileId = UUID.randomUUID().toString();
        String fileName = sourceFile.getOriginalFilename(); // 영수증2
        String filePath = fileWriter.getFilePath(fileName, sourceFile); // home/image/영수증2.jpg
        log.info("fileName : {}, filePath : {}", fileName, filePath);
       /* File file = new File(filePath);
        boolean isExists = file.exists();
        log.info("fileName :: {}", fileName);
        log.info("filePath:: {}", filePath );
        log.info("sourceFile:: {}", sourceFile.getOriginalFilename() );

        if(isExists) {
           throw new AppException(ErrCode.err_duplicate_file, "파일이름을 수정해주세요.");
        }*/
        fileWriter.writeFile(sourceFile, filePath);
        log.info("###########################");
        return ImageFile.builder()
                .fileName(sourceFile.getOriginalFilename())
                .filePath(filePath)
                .fileId(sourceFile.getOriginalFilename())
                .fileSize(sourceFile.getSize())
                .build();
    }

    public String imagePath(MultipartFile sourceFile) {
        String fileName = sourceFile.getOriginalFilename();
        String filePath = fileWriter.getFilePath(fileName, sourceFile);
        return filePath;
    }

    public Resource loadFile (String fileName) throws FileNotFoundException {
        try {
            Path file = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new AppException(ErrCode.err_file_not_found, fileName + "을 찾을 수 없습니다.");
            }
        } catch (MalformedURLException exception) {
            log.error("error:: ", exception);
            throw new AppException(ErrCode.err_file_not_found, fileName + "을 찾을 수 없습니다.");
        }
    }
}
