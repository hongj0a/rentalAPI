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
    private Long rental_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
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
    private Integer rental_price;

    @NotNull(message = "상품설명은 Null일 수 없습니다.")
    @Lob
    private String content;

    @ColumnDefault("0")
    private Integer trading_method;

    @ColumnDefault("0")
    private Integer like_cnt;

    @ColumnDefault("0")
    private Integer view_cnt;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date complete_at;

    @ColumnDefault("1")
    private boolean active_yn;

    @ColumnDefault("0")
    private boolean delete_yn;

    @Column
    private Date delete_at;

    @Column
    private String updator;
}
