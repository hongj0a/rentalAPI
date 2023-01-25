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
@Table(name = "block_user")
@Entity
@DynamicInsert
public class BlockUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "block_idx")
    private Long blockIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @Column
    private String reason;

    @Column (name = "reporter_idx")
    @NotNull(message = " 차단하는 사용자의 값이 Null일 수 없습니다. ")
    private Integer reporterIdx;

    @ColumnDefault("0")
    @Column (name = "processing_status")
    private Integer processingStatus;
    
    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;

}
