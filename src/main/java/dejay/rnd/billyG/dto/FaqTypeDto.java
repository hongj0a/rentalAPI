package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.Admin;
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
    private boolean deleteYn;
    private Date createAt;
    private Date updateAt;
    private String updator;

    public FaqTypeDto(FaqType faqType){
        faqTypeIdx = faqType.getFaqTypeIdx();
        typeName = faqType.getTypeName();
        orderNum = faqType.getOrderNum();
        createAt = faqType.getCreateAt();
        updateAt = faqType.getUpdateAt();
        updator = faqType.getUpdator();
    }
    public FaqType toEntity(){
        return FaqType.builder()
                .faqTypeIdx(faqTypeIdx)
                .typeName(typeName)
                .orderNum(orderNum)
                .deleteYn(deleteYn)
                .createAt(createAt)
                .updateAt(updateAt)
                .updator(updator)
                .build();
    }
}
