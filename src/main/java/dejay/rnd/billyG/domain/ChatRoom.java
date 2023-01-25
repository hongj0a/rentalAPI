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
@Table(name = "chat_room")
@Entity
@DynamicInsert
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "chat_room_idx")
    private Long chatRoomIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "rentalIdx")
    private Rental rental;

    /**
     * status 정의
     * 0 : 매칭 대기중..
     * 1 : 매칭 완료
     * 2 : 결제 대기중
     * ...
     */
    @ColumnDefault("0")
    @Column (name = "seller_status")
    private boolean sellerStatus;

    @ColumnDefault("0")
    @Column (name = "buyer_status")
    private boolean buyerStatus;

    @Column (name = "seller_idx")
    @NotNull
    private Integer sellerIdx;

    @Column (name = "buyer_idx")
    @NotNull
    private Integer buyerIdx;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

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
