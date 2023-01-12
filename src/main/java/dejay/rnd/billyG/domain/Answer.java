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
    @NotNull
    @JoinColumn (name = "ONE_IDX")
    private One_To_OneInquiry one_to_one_inquiry;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    // 답변상태 1:완료 0:대기
    // 관리자가 답변을 남기고 노출을 시키는 시점에 완료로 변경
    @ColumnDefault("0")
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

    //status 바뀌는 시점에 timestamp.
    @Column
    private Date update_at;

    @Column
    private Date delete_at;

    @Column
    private String updator;

}
