package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.Admin;
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
    private boolean deleteYn;
    private boolean activeYn;
    private Date createAt;
    private Date updateAt;
    private String updator;

    public FaqDto(Faq faq){
        faqIdx = faq.getFaqIdx();
        faqType = faq.getFaqType();
        title = faq.getTitle();
        content = faq.getContent();
        createAt = faq.getCreateAt();
        updateAt = faq.getUpdateAt();
        updator = faq.getUpdator();
    }
    public Faq toEntity(){
        return Faq.builder()
                .faqIdx(faqIdx)
                .faqType(faqType)
                .title(title)
                .content(content)
                .deleteYn(deleteYn)
                .activeYn(activeYn)
                .createAt(createAt)
                .updateAt(updateAt)
                .updator(updator)
                .build();
    }
}
