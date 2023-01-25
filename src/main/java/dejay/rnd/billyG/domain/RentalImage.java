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
public class RentalImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "image_idx")
    private Long imageIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "rentalIdx")
    private Rental rental;

    @Column (length = 1000, name = "image_url")
    private String imageUrl;

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
