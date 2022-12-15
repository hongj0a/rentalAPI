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
@Table(name = "review")
@Entity
@DynamicInsert
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_idx;

    @ManyToOne
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @ManyToOne
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ColumnDefault("0")
    private Integer review_score;

    @Column
    @Lob
    private String review_content;

    @ColumnDefault("0")
    private boolean delete_yn;

    @ColumnDefault("1")
    private boolean active_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

}
