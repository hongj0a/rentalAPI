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
public class ChatContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "chat_idx")
    private Long chatIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "chatRoomIdx")
    private ChatRoom chatRoom;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "userIdx")
    private User user;

    @ManyToOne
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    @Column
    private String type;

    @Column
    private String content;

    @ColumnDefault("0")
    @Column (name = "check_yn")
    private boolean checkYn;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

}
