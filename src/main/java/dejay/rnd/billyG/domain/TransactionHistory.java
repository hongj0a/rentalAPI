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
@Table(name = "transaction_history")
@Entity
@DynamicInsert
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "history_idx")
    private Long historyIdx;

    @Column (name = "transaction_idx")
    private Long transactionIdx;

    @Column (name = "user_idx")
    private Long userIdx;

    @Column (name = "rental_idx")
    private Long rentalIdx;

    @Column (name = "cancel_yn")
    private boolean cancelYn;

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
     *      * 0 : 매칭대기중
     *      * 1 : 결제대기중
     *      * 2 : 배송완료
     *      * 3 : 반납완료...
     */
    @Column (name = "owner_status")
    private Integer ownerStatus;

    /**
     *  * 렌터 상태
     *      * 0 : 매칭대기중
     *      * 1 : 결제대기중
     *      * 2 : 배송완료
     *      * 3 : 반납완료...
     */
    @Column (name = "renter_status")
    private Integer renterStatus;

    @Column
    private String updator;

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
