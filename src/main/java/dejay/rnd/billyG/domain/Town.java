package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "town")
@Entity
@DynamicInsert
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long town_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @Column
    private String town_name;

    @ColumnDefault("0")
    private boolean delete_yn;

    @ColumnDefault("0")
    private boolean auth_status;

    @ColumnDefault("0")
    private Integer auth_count;

    //0 관심지역 1 대표지역
    @ColumnDefault("0")
    private boolean lead_town;

    @Column( precision = 18, scale = 10 )
    private BigDecimal region_latitue;

    @Column( precision = 18, scale = 10 )
    private BigDecimal region_longitude;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

    @Column
    private String updator;

}
