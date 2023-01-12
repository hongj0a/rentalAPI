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
@Table(name = "chat_content")
@Entity
@DynamicInsert
public class Chat_Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "CHAT_ROOM_IDX")
    private Chat_Room chat_room;

    @Column
    @NotNull (message = "user_idx는 Null일 수 없습니다.")
    private Integer user_idx;

    @Column
    @Lob
    @NotNull (message = "채팅 내용을 입력해 주세요.")
    private String content;

    @ColumnDefault("0")
    private boolean check_yn;

    @Column
    @Lob
    private String image_url;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private String updator;
}
