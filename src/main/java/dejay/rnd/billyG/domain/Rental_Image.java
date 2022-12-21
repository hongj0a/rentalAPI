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
@Table(name = "rental_image")
@Entity
@DynamicInsert
public class Rental_Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @Column (length = 1000)
    private String image_url;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;
    

}
