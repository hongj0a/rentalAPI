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
@Table(name = "likes")
@Entity
@DynamicInsert
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long like_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ManyToOne
    @NotNull
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

    @Column
    private String updator;
}
