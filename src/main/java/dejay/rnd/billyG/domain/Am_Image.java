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
@Table(name = "am_image")
@Entity
@DynamicInsert
public class Am_Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "AM_IDX")
    private Arbitration_Management arbitration_management;

    @Column
    @Lob
    private String image_url;

    @Column
    private String updator;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    //status 바뀌는 시점에 timestamp.
    private Date update_at;

}
