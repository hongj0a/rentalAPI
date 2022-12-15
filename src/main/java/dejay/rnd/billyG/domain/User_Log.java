package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_log")
@Entity
@DynamicInsert
public class User_Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long log_idx;

    @ManyToOne
    @JoinColumn (name = "USER_IDX")
    private User user;

    @Column
    private String content;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

}
