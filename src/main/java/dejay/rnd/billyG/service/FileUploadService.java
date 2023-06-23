package dejay.rnd.billyG.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import dejay.rnd.billyG.config.ImageProperties;
import dejay.rnd.billyG.model.FileWriter;
import dejay.rnd.billyG.model.ImageFile;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.except.AppException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
@Service
public class FileUploadService {
    private FileWriter fileWriter;
    private final Path fileStorageLocation;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;

    @Autowired
    FileUploadService(ImageProperties imageProperties, FileWriter fileWriter, AmazonS3Client amazonS3Client) {
        this.fileStorageLocation = Paths.get(imageProperties.getDefaultPath())
                .toAbsolutePath().normalize();
        this.fileWriter = fileWriter;
        this.amazonS3Client = amazonS3Client;
    }


    public ImageFile upload(MultipartFile sourceFile) throws IOException{

        LocalDateTime now = LocalDateTime.now();

        String fileId = UUID.randomUUID().toString();
        String fileName = sourceFile.getOriginalFilename();

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = now + "_" + fileId + "." + ext;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(sourceFile.getContentType());
        objectMetadata.setContentLength(sourceFile.getSize());

        String key = "image/" + fileName;

        try (InputStream inputStream = sourceFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String storeFileUrl = amazonS3Client.getUrl(bucketName, key).toString();

        String filePath = fileWriter.getFilePath(fileName, sourceFile);
        uploadMultipart(sourceFile, storeFileUrl);

        return ImageFile.builder()
                .fileName(fileName)
                .filePath(storeFileUrl)
                .fileId(fileId)
                .fileSize(sourceFile.getSize())
                .build();
    }



    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String uploadMultipart(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return uploadS3(uploadFile, dirName);
    }

    private String uploadS3(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucketName, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws  IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
    
    
    
    
    
}
