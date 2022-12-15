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
@Table(name = "like")
@Entity
@DynamicInsert
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long like_idx;

    @ManyToOne
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ManyToOne
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @ColumnDefault("0")
    private boolean delete_yn;

    @Column
    private Date delete_at;

}
