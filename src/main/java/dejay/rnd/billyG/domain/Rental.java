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
@Table(name = "rental")
@Entity
@DynamicInsert
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "rental_idx")
    private Long rentalIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @NotNull
    @Column (name = "owner_name")
    private String ownerName;

    @Column (name = "renter_name")
    @ColumnDefault("'-'")
    private String renterName;

    @ColumnDefault("0")
    private Integer status;

    @Column
    @NotNull(message = "제목은 Null일 수 없습니다.")
    private String title;

    @NotNull(message = " 보증금은 Null일 수 없습니다. ")
    @ColumnDefault("0")
    private Integer deposit;

    @NotNull(message = " 렌탈료는 Null일 수 없습니다. ")
    @ColumnDefault("0")
    @Column (name = "rental_price")
    private Integer rentalPrice;

    @NotNull(message = "상품설명은 Null일 수 없습니다.")
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

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "complete_at")
    private Date completeAt;

    @ColumnDefault("1")
    @Column (name = "active_yn")
    private boolean activeYn;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name ="pull_up_at")
    private Date pullUpAt;

    @Column(name ="pull_up_cnt")
    @ColumnDefault("0")
    private Integer pullUpCnt;
}
