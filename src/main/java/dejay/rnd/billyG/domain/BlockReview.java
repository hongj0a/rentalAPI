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
@Table(name = "block_review")
@Entity
@DynamicInsert
public class BlockReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_review_idx")
    private Long blockReviewIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "blockTypeIdx")
    private BlockType blockType;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "reviewIdx")
    private Review review;

    @Column
    private String reason;

    @Column (name = "reporter_idx")
    private Long reporterIdx;

    @ColumnDefault("0")
    @Column (name = "processing_status")
    private Integer processingStatus;

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
