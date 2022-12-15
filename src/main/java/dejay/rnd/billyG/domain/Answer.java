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
@Table(name = "answer")
@Entity
@DynamicInsert
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answer_idx;

    @ManyToOne
    @JoinColumn (name = "ONE_IDX")
    private OneToOneInquiry one_to_one_inquiry;

    @ManyToOne
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;


    @ColumnDefault("0")
    // 답변상태 1:완료 0:대기
    // 관리자가 답변을 남기고 노출을 시키는 시점에 완료로 변경
    private boolean status;

    @Column
    @Lob
    private String answer_content;

    @ColumnDefault("0")
    private boolean delete_yn;

    @ColumnDefault("1")
    private boolean active_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    //status 바뀌는 시점에 timestamp.
    private Date update_at;

    @Column
    private Date delete_at;

}
