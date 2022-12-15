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
@Table(name = "user")
@Entity
@DynamicInsert
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_idx;

    @NotNull(message = " user 이름은 Null 일 수 없습니다.")
    private String nick_name;

    @NotNull(message = " user email은 Null 일 수 없습니다. ")
    private String id_email;

    @Column
    private String email;

    @NotNull(message = "전화번호는 Null일 수 없습니다.")
    private String phone_num;

    @Column(length = 1000)
    private String profile_image_url;

    @ColumnDefault("1")
    private Integer user_level;

    @ColumnDefault("0")
    private Integer activity_score;

    @NotNull(message = "sns type은 Null일 수 없습니다.")
    private String sns_type;

    @NotNull(message = "token값은 Null일 수 없습니다.")
    //long text
    @Lob
    private String token;

    @NotNull(message = "token값은 Null일 수 없습니다.")
    @Lob
    private String push_token;

    @ColumnDefault("1")
    private boolean active_yn;

    @ColumnDefault("0")
    private boolean delete_yn;

    @ColumnDefault("0")
    private boolean block_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

    @Column
    private Date block_at;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date levelup_at;

    @ColumnDefault("0")
    private Integer star_point;

    @ColumnDefault("0")
    private boolean do_not_disturb_time_yn;

    @Column
    private Date do_not_disturb_start_time;

    @Column
    private Date do_not_disturb_end_time;

    @ColumnDefault("0")
    private boolean chat_notice_yn;

    @ColumnDefault("0")
    private boolean activity_notice_yn;

    @ColumnDefault("0")
    private boolean marketing_notice_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date last_login_date;

    @Column(length = 1000)
    private String ci_value;

}
