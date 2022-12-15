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
public class Chat_Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chat_room_idx;

    @ManyToOne
    @JoinColumn (name = "RENTAL_IDX")
    private Rental rental;

    @ColumnDefault("0")
    private boolean seller_status;

    @ColumnDefault("0")
    private boolean buyer_status;

    @Column
    private Integer seller_idx;

    @Column
    private Integer buyer_idx;

    @ColumnDefault("0")
    private boolean delete_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

}
