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
@Table(name = "notice_type")
@Entity
@DynamicInsert
public class NoticeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notice_type_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    @NotNull (message = "타입명은 Null일 수 없습니다.")
    private String type_name;

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
