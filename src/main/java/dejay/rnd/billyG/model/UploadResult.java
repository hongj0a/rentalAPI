package dejay.rnd.billyG.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult {

    private String fileId;
    private String fileName;
    private Long fileSize;

}
