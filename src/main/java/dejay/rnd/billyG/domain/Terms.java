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
@Table(name = "terms")
@Entity
@DynamicInsert
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "terms_idx")
    private Long termsIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    /**
     * 약관 분류값
     * 0 : 개인정보처리방침
     * 1 : 이용약관
     * 2 : 마케팅동의
     * 3 : 제3자정보제공
     * 4 : 위치기반
     * ...
     */
    @Column
    @NotNull
    private Integer type;

    @NotNull (message = "제목은 Null일 수 없습니다.")
    private String title;

    @NotNull (message = "내용은 Null일 수 없습니다.")
    @Lob
    private String content;

    @NotNull (message = "version은 Null일 수 없습니다.")
    private String version;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column (name = "major_changes")
    @Lob
    private String majorChanges;

    @Column
    private String updator;

    @Column (name = "reservation_date")
    private Date reservationDate;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

}
