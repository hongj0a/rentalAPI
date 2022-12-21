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
@Table(name = "transaction")
@Entity
@DynamicInsert
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @ManyToOne
    @NotNull
    //빌려간 사람의 idx
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ColumnDefault("0")
    private boolean delete_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

}
