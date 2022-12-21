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
@Table(name = "user_count")
@Entity
@DynamicInsert
public class User_Count {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long count_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ColumnDefault("0")
    private Long receive_review_cnt;

    @ColumnDefault("0")
    private Long give_review_cnt;

    @ColumnDefault("0")
    private Long login_cnt;

    @ColumnDefault("0")
    private Long cell_cnt;

    @ColumnDefault("0")
    private Long buy_cnt;

    @ColumnDefault("0")
    private Long block_user_cnt;

    @ColumnDefault("0")
    private Long block_post_cnt;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

}
