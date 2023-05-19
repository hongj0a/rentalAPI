package dejay.rnd.billyG.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageFile {
    private String fileId;
    private Long fileSize;
    private String fileName;
    private String fileType;
    private String filePath;
}
