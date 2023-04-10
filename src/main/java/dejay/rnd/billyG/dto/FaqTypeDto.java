package dejay.rnd.billyG.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqTypeDto {
    private Long faqTypeIdx;
    private String typeName;
    private Integer orderNum;
    private Boolean deleteYn;
    private Date createAt;
    private Date updateAt;
    private String updator;

}
