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
    @Column (name = "user_idx")
    private Long userIdx;

    @NotNull(message = " user 이름은 Null 일 수 없습니다.")
    @Column (name = "nick_name")
    private String nickName;

    @NotNull(message = " user email은 Null 일 수 없습니다. ")
    @Column (name = "id_email")
    private String idEmail;

    @Column
    private String email;

    @NotNull(message = "전화번호는 Null일 수 없습니다.")
    @Column (name = "phone_num")
    private String phoneNum;

    @Column(length = 1000, name = "profile_image_url")
    private String profileImageUrl;

    @ColumnDefault("1")
    @Column (name = "user_level")
    private Integer userLevel;

    @ColumnDefault("0")
    @Column (name = "activity_score")
    private Integer activityScore;

    @NotNull(message = "sns type은 Null일 수 없습니다.")
    @Column (name = "sns_type")
    private String snsType;

    @NotNull(message = "token값은 Null일 수 없습니다.")
    //long text
    @Lob
    private String token;

    @NotNull(message = "token값은 Null일 수 없습니다.")
    @Lob
    @Column (name = "push_token")
    private String pushToken;

    @ColumnDefault("1")
    @Column (name = "active_yn")
    private boolean activeYn;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @ColumnDefault("0")
    @Column (name = "block_yn")
    private boolean blockYn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column (name = "block_at")
    private Date blockAt;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "levelup_at")
    private Date levelupAt;

    @ColumnDefault("0")
    @Column (name = "star_point")
    private Integer starPoint;

    @ColumnDefault("0")
    @Column (name = "do_not_disturb_time_yn")
    private boolean doNotDisturbTimeYn;

    @Column (name = "do_not_disturb_start_time")
    private Date doNotDisturbStartTime;

    @Column (name = "do_not_disturb_end_time")
    private Date doNotDisturbEndTime;

    @ColumnDefault("0")
    @Column (name = "chat_notice_yn")
    private boolean chatNoticeYn;

    @ColumnDefault("0")
    @Column (name = "activity_notice_yn")
    private boolean activityNoticeYn;

    @ColumnDefault("0")
    @Column (name = "marketing_notice_yn")
    private boolean marketingNoticeYn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "last_login_date")
    private Date lastLoginDate;

    @Column(length = 1000, name = "ci_value")
    private String ciValue;

    @Column (name = "invitation_code")
    @NotNull (message = " 초대코드 랜덤값은 null일 수 없습니다. ")
    private String invitationCode;

    @ColumnDefault("0")
    @Column (name = "billy_pay")
    private String billyPay;

    @Column (name = "device_id")
    @NotNull (message = "디바이스 아이디는 null일 수 없습니다.")
    private String deviceId;

}
