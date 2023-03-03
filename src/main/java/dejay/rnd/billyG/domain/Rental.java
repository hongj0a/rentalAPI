package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rental")
@Entity
@DynamicInsert
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "rental_idx")
    private Long rentalIdx;

    @OneToMany(mappedBy = "rental")
    private List<RentalTownInfo> rentalTownInfos = new ArrayList<>();

    @OneToMany(mappedBy = "rental")
    private List<RentalCategoryInfo> rentalCategoryInfos = new ArrayList<>();

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @Column (name = "owner_name")
    private String ownerName;

    @Column (name = "renter_name")
    @ColumnDefault("'-'")
    private String renterName;

    @ColumnDefault("0")
    private Integer status;

    @Column
    private String title;

    @ColumnDefault("0")
    private Integer deposit;

    @ColumnDefault("0")
    @Column (name = "rental_price")
    private Integer rentalPrice;

    @Lob
    private String content;

    @ColumnDefault("0")
    @Column (name = "trading_method")
    private Integer tradingMethod;

    @ColumnDefault("0")
    @Column (name = "like_cnt")
    private Integer likeCnt;

    @ColumnDefault("0")
    @Column (name = "view_cnt")
    private Integer viewCnt;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "complete_at")
    private Date completeAt;

    @Column (name = "active_yn")
    private boolean activeYn = true;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name ="pull_up_at")
    private Date pullUpAt;

    @Column(name ="pull_up_cnt")
    @ColumnDefault("0")
    private Integer pullUpCnt;
}
