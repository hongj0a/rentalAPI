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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.except.AppException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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


    public ImageFile upload(MultipartFile sourceFile) {

        LocalDateTime now = LocalDateTime.now();

        String fileId = UUID.randomUUID().toString();
        String fileName = sourceFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        fileName = now + "_" + fileId + "." + ext;
        System.out.println("fileName = " + fileName);

        String filePath = fileWriter.getFilePath(fileName, sourceFile);

        fileWriter.writeFile(sourceFile, filePath);
        return ImageFile.builder()
                .fileName(fileName)
                .filePath(filePath)
                .fileId(fileId)
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
