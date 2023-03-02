package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;
import java.util.Set;


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

    @Column (name = "nick_name")
    private String nickName;

    @Column (name = "id_email")
    private String idEmail;

    @Column (name = "email", unique = true)
    private String email;

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

    @Column (name = "sns_type")
    private String snsType;

    @Column (name = "sns_name")
    private String snsName;

    //long text
    @Lob
    @Column (name = "access_token")
    private String accessToken;

    //long text
    @Lob
    @Column (name = "refresh_token")
    private String refreshToken;

    @Lob
    @Column (name = "push_token")
    private String pushToken;

    @Builder.Default
    @Column (name = "active_yn")
    private boolean activeYn = true;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @ColumnDefault("0")
    @Column (name = "block_yn")
    private boolean blockYn;

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

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "last_login_date")
    private Date lastLoginDate;

    @Column(length = 1000, name = "ci_value")
    private String ciValue;

    @Column (name = "invitation_code")
    private String invitationCode;

    @ColumnDefault("0")
    @Column (name = "billy_pay")
    private String billyPay;

    @Column (name = "device_id")
    private String deviceId;

    @Column (name = "grade_idx")
    private Integer gradeIdx;

    @ManyToOne
    @JoinColumn (name = "outIdx")
    private MemberOutType memberOutType;

    @Column (name = "out_reason", length = 1000)
    private String outReason;

    @Column (name = "name")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_grade",
            joinColumns = {@JoinColumn(name = "user_idx", referencedColumnName = "user_idx")},
            inverseJoinColumns = {@JoinColumn(name = "grade_name", referencedColumnName = "grade_name")})
    private Set<Grade> grades;

}
