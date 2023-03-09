package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.Faq;
import dejay.rnd.billyG.domain.FaqType;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqDto {

    private Long faqIdx;
    private FaqType faqType;
    private String title;
    private String content;
    private Boolean deleteYn;
    private Boolean activeYn;
    private Date createAt;
    private Date updateAt;
    private String updator;

    public FaqDto(Faq faq){
        faqIdx = faq.getFaqIdx();
        faqType = faq.getFaqType();
        title = faq.getTitle();
        content = faq.getContent();
        deleteYn = faq.getDeleteYn();
        activeYn = faq.getActiveYn();
        createAt = faq.getCreateAt();
        updateAt = faq.getUpdateAt();
        updator = faq.getUpdator();
    }
}
