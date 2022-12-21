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
@Table(name = "my_attention")
@Entity
@DynamicInsert
public class My_Attention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attention_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "USER_IDX")
    private User user;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "CATEGORY_IDX")
    private Category category;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @ColumnDefault("0")
    private boolean delete_yn;

    @Column
    private Date delete_at;

}
