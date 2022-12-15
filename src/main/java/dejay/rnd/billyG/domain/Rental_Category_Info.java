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
@Table(name = "rental_category_info")
@Entity
@DynamicInsert
public class Rental_Category_Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rental_category_info_idx;

    @ManyToOne
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @ManyToOne
    @JoinColumn (name = "CATEGORY_IDX")
    private Category category;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

}
