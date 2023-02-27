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
@Table(name = "rental_town_info")
@Entity
@DynamicInsert
public class RentalTownInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "rental_town_info_idx")
    private Long rentalTownInfoIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn (name = "rentalIdx")
    private Rental rental;

    @Column (name = "town_name")
    private String townName;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column
    private String updator;
}
