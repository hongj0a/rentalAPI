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
@Table(name = "block_post")
@Entity
@DynamicInsert
public class Block_Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long block_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @Column
    private String reason;

    @NotNull(message = " 차단하는 사용자의 값이 Null일 수 없습니다. ")
    private Integer reporter_idx;

    @ColumnDefault("0")
    private Integer processing_status;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @ColumnDefault("0")
    private boolean delete_yn;

    @Column
    private Date delete_at;

}
