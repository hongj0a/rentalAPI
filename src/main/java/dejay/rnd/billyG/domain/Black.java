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
@Table(name = "black")
@Entity
@DynamicInsert
public class Black {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long black_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    @Column
    private String reason;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    @NotNull(message = " 가입일자는 Null일 수 없습니다.")
    private Date sign_at;

    @Column
    private Date update_at;

    @ColumnDefault("0")
    private boolean delete_yn;

    @Column
    private Date delete_at;

}
