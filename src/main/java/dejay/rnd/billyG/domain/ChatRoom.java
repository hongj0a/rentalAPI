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
@Table(name = "chat_room")
@Entity
@DynamicInsert
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "chat_room_idx")
    private Long chatRoomIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "rentalIdx")
    private Rental rental;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "transactionIdx")
    private Transaction transaction;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "toIdx", referencedColumnName = "user_idx")
    private User toUser;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "fromIdx", referencedColumnName = "user_idx")
    private User fromUser;


    @Column(name = "last_chat_message")
    private String lastChatMessage;

    @Column(name = "visible_to")
    private Integer visibleTo;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column
    private String updator;

}
