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
@Table(name = "terms")
@Entity
@DynamicInsert
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long terms_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    @NotNull (message = "제목은 Null일 수 없습니다.")
    private String title;

    @NotNull (message = "내용은 Null일 수 없습니다.")
    @Lob
    private String content;

    @NotNull (message = "version은 Null일 수 없습니다.")
    private String version;

    @ColumnDefault("1")
    private boolean active_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

}
