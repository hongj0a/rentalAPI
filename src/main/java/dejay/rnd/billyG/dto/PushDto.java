package dejay.rnd.billyG.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushDto {
    private Long userIdx;
    private Long adminIdx;
    private Long[] hostIdxes;
    private String title;
    private String message;
    private Long targetIdx;
    private Long targetIdx2;
    private int type;
    private String topicType;
}
