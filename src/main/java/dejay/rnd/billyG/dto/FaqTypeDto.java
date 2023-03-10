package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.FaqType;
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

    public FaqTypeDto(FaqType faqType){
        faqTypeIdx = faqType.getFaqTypeIdx();
        typeName = faqType.getTypeName();
        orderNum = faqType.getOrderNum();
        deleteYn = faqType.getDeleteYn();
        createAt = faqType.getCreateAt();
        updateAt = faqType.getUpdateAt();
        updator = faqType.getUpdator();
    }

}
