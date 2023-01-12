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
@Table(name = "transaction")
@Entity
@DynamicInsert
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    //빌려간 사람의 idx
    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ColumnDefault("0")
    private boolean cancel_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date pay_at;

    @Column
    private Date cancel_at;

    @Column
    private Date return_at;

    /**
     *  * 렌탈오너 상태
     *      * 0 : 매칭대기중
     *      * 1 : 결제대기중
     *      * 2 : 배송완료
     *      * 3 : 반납완료...
     */
    @Column
    @NotNull
    @ColumnDefault("0")
    private Integer owner_status;

    /**
     *  * 렌터 상태
     *      * 0 : 매칭대기중
     *      * 1 : 결제대기중
     *      * 2 : 배송완료
     *      * 3 : 반납완료...
     */
    @Column
    @NotNull
    @ColumnDefault("0")
    private Integer renter_status;

    @Column
    private String updator;

    @ColumnDefault("0")
    private boolean return_yn;

    @Column
    private Integer coupon_idx;

    @Column (length = 100)
    private String mypay_fee;

    @Column (length = 100)
    private String coupon_fee;

    @Column (length = 100)
    private String final_pay_fee;

    /**
     * 결제방식
     * 0 : 계좌이체
     * 1 : 카드결제
     * 2 : 무통장입금
     */
    @Column (length = 100)
    private Integer pay_method;

    @Column (length = 100)
    private String return_bankname;

    @Column (length = 100)
    private String return_banknum;

}
