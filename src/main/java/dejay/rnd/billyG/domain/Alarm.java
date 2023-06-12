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
@Table(name = "alarm")
@Entity
@DynamicInsert
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "alarm_idx")
    private Long alarmIdx;

    @ManyToOne
    @JoinColumn (name = "userIdx")
    private User user;

    @ManyToOne
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    @ColumnDefault("0")
    @Column (name = "read_yn")
    private boolean readYn;

    @Column
    private String content;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column (name = "rental_idx")
    private Long rentalIdx;

    @Column (name = "review_idx")
    private Long reviewIdx;

    @Column (name = "chat_idx")
    private Long chatIdx;

    @Column (name = "host_idx")
    private Long hostIdx;
}
