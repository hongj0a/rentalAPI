package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
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
    @Column (name = "town_idx")
    private Long townIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @Column (name = "town_name")
    private String townName;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @ColumnDefault("0")
    @Column (name = "auth_status")
    private boolean authStatus;

    @ColumnDefault("0")
    @Column (name = "auth_count")
    private Integer authCount;

    //0 관심지역 1 대표지역
    @ColumnDefault("0")
    @Column (name = "lead_town" )
    private boolean leadTown;

    @Column( precision = 18, scale = 10 , name = "region_latitue")
    private BigDecimal regionLatitue;

    @Column( precision = 18, scale = 10 , name = "region_longitude")
    private BigDecimal regionLongitude;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;

}
