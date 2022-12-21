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
@Table(name = "admin")
@Entity
@DynamicInsert
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_idx;

    @NotNull(message = " admin id는 Null 일 수 없습니다.")
    private String id;

    @NotNull(message = " admin password는 Null 일 수 없습니다. ")
    private String password;

    @ColumnDefault("1")
    private Integer admin_level;

    @ColumnDefault("1")
    private boolean active_yn;

    @NotNull(message = " admin 이름은 Null 일 수 없습니다.")
    private String name;

    @NotNull(message = " admin 닉네임은 Null 일 수 없습니다.")
    private String nick_name;

    @ColumnDefault("0")
    private boolean delete_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date levelup_at;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date last_login_date;

    @Lob
    //접속허용 ip 관리 필드
    private String ip_value;

    @Column
    private Date delete_at;
}
