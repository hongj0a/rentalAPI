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
    @JoinColumn (name = "CHAT_ROOM_IDX")
    private Chat_Room chat_room;

    @Column
    private Integer user_idx;

    @Column
    @Lob
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

}
