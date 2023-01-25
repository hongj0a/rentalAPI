package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rental_history")
@Entity
@DynamicInsert
public class RentalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "history_idx")
    private Long historyIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "rentalIdx")
    private Rental rental;

    @Column(length = 1000)
    private String content;

    @Column
    private String owner;

    @Column
    private String renter;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

}
