package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.Notice;

import java.util.Date;

public class NoticeDto {

    private Long noticeIdx;

    private Integer noticeType;

    private String title;

    private String content;

    private Date createAt;

    private Date updateAt;

    private Date deleteAt;

    private String updator;

    private Boolean deleteYn;

    private Boolean activeYn;

    public  NoticeDto (Notice notice){
        noticeIdx = notice.getNoticeIdx();
        noticeType = notice.getNoticeType();
        title = notice.getTitle();
        content = notice.getContent();
        createAt = notice.getCreateAt();
        updateAt = notice.getUpdateAt();
        deleteAt = notice.getDeleteAt();
        updator = notice.getUpdator();
    }


    public Notice toEntity() {
        return Notice.builder()
                .noticeIdx(noticeIdx)
                .noticeType(noticeType)
                .title(title)
                .content(content)
                .createAt(createAt)
                .updateAt(updateAt)
                .deleteAt(deleteAt)
        .build();
    }
}
