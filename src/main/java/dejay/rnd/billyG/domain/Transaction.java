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
    @Column (name = "transaction_idx")
    private Long transactionIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "rentalIdx")
    private Rental rental;

    //빌려간 사람의 idx
    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @ColumnDefault("0")
    @Column (name = "cancel_yn")
    private boolean cancelYn;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "pay_at")
    private Date payAt;

    @Column (name = "cancel_at")
    private Date cancelAt;

    @Column (name = "return_at")
    private Date returnAt;

    /**
     *  * 렌탈오너 상태
     *      * 10 : 매칭대기
     *      * 30 : 렌탈중
     *      * 60 : 이의신청
     *      * 70 : 렌탈완료
     */
    @Column (name = "owner_status")
    @ColumnDefault("0")
    private Integer ownerStatus;

    /**
     *  * 렌터 상태
     *      * 10 : 매칭대기
     *      * 20 : 매칭완료
     *      * 40 : 물품인수
     *      * 50 : 물품반납
     *      * 70 : 렌탈완료
     */
    @Column (name = "renter_status")
    @ColumnDefault("0")
    private Integer renterStatus;

    @Column
    private String updator;

    @ColumnDefault("0")
    @Column (name = "return_yn")
    private boolean returnYn;

    @Column (name = "coupon_idx")
    private Integer couponIdx;

    @Column (length = 100, name = "mypay_fee")
    private String myPayFee;

    @Column (length = 100, name = "coupon_fee")
    private String couponFee;

    @Column (length = 100 , name = "final_pay_fee")
    private String finalPayFee;

    /**
     * 결제방식
     * 0 : 계좌이체
     * 1 : 카드결제
     * 2 : 무통장입금
     */
    @Column (length = 100 , name = "pay_method")
    private Integer payMethod;

    @Column (name = "complete_at")
    private Date completeAt;
}
