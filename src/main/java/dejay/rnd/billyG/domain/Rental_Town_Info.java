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
public class Rental_Town_Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rental_town_info_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @Column
    private String town_name;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;
    

}
