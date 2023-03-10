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
@Table(name = "arbitration_management")
@Entity
@DynamicInsert
public class ArbitrationManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "am_idx")
    private Long amIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "oneIdx")
    private OneToOneInquiry oneToOneInquiry;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    // 답변상태 1:완료 0:대기
    // 관리자가 답변을 남기고 노출을 시키는 시점에 완료로 변경
    @ColumnDefault("0")
    private boolean status;

    @Column (name = "answer_content")
    @Lob
    private String answerContent;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column (name = "active_yn")
    private boolean activeYn = true;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    //status 바뀌는 시점에 timestamp.
    private Date updateAt;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;

}
