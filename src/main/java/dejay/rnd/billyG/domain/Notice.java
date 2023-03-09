package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notice")
@Entity
@DynamicInsert
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "notice_idx")
    private Long noticeIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    /**
     * 공지사항 타입 고정값, 하드코딩
     * 0. 공지
     * 1. 이벤트
     * 2. 채용
     */
    @NotNull
    @Column (name = "notice_type")
    private Integer noticeType;

    @Column
    private String title;

    @Column
    @Lob
    private String content;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private Boolean deleteYn;

    @ColumnDefault("1")
    @Column (name = "active_yn")
    private Boolean activeYn;

    @ColumnDefault("0")
    @Column (name = "main_yn")
    private Boolean mainYn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;

}
