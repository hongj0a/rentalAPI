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
@Table(name = "user_history")
@Entity
@DynamicInsert
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name ="history_idx")
    private Long historyIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    @Column(length = 1000)
    private String title;

    @Column
    @Lob
    private String content;

    @Column
    private Integer status;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

}
