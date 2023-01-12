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
@Table(name = "coupon_management")
@Entity
@DynamicInsert
public class Coupon_Management {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupon_manage_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "COUPON_IDX")
    private Coupon coupon;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ColumnDefault("0")
    private boolean delete_yn;

    @ColumnDefault("0")
    private boolean use_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

    @Column
    private Date use_at;

    @Column
    private String updator;

}
