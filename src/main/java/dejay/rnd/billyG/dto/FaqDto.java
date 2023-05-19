package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.Category;
import dejay.rnd.billyG.domain.Faq;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqDto {

    private Long faqIdx;
    private Category category;
    private String title;
    private String content;
    private Boolean deleteYn;
    private Boolean activeYn;
    private Date createAt;
    private Date updateAt;
    private String updator;

    public FaqDto(Faq faq){
        faqIdx = faq.getFaqIdx();
        category = faq.getCategory();
        title = faq.getTitle();
        content = faq.getContent();
        deleteYn = faq.getDeleteYn();
        createAt = faq.getCreateAt();
        updateAt = faq.getUpdateAt();
        updator = faq.getUpdator();
    }
}
