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
@Table(name = "user_memo")
@Entity
@DynamicInsert
public class User_Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memo_idx;

    @ManyToOne
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ManyToOne
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    @Column(length = 10000)
    private String memo;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @ColumnDefault("0")
    private boolean delete_yn;

    @Column
    private Date delete_at;

}
