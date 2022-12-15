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
@Table(name = "review_image")
@Entity
@DynamicInsert
public class Review_Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_idx;

    @ManyToOne
    @JoinColumn (name = "REVIEW_IDX")
    private Review review;

    @Column (length = 1000)
    private String image_url;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;


}
