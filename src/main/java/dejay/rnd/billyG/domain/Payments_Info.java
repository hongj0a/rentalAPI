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
@Table(name = "payments_info")
@Entity
@DynamicInsert
public class Payments_Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payments_info_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "TRANSACTION_IDX")
    private Transaction transaction;

    @Column
    private Date pay_request_date;

    @Column
    private Date pay_finished_date;

    @Column
    private Date update_at;

    @Column
    private String updator;

    @Column (length = 100)
    private Integer renter_price;

    @Column (length = 100)
    private Integer rental_owner_price;

    @Column
    @Lob
    private String reason;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

}
