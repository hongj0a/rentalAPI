package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_item")
@Entity
@DynamicInsert
public class User_item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_item_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ITEMS_IDX")
    private Evaluation_Items evaluation_items;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

}
