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
@Table(name = "status_history")
@Entity
@DynamicInsert
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "status_idx")
    private Long likeIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    /**
     * 회원상태값
     * 10 : 활동회원
     * 20 : 휴면회원
     * 30 : 탈퇴회원
     * 40 : 블랙리스트 회원
     */
    @Column (name = "active_at")
    private Date activeAt;

    @Column (name = "dormancy_at")
    private Date dormancyAt;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column (name = "black_at")
    private Date blackAt;

    @Column
    private String updator;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;
}
