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
@Table(name = "one_to_one_type")
@Entity
@DynamicInsert
public class OneToOneType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "one_type_idx")
    private Long oneTypeIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    @Column (name = "type_name")
    @NotNull (message = "타입명은 Null일 수 없습니다.")
    private String typeName;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "delete_at")
    private Date deleteAt;

    @Column
    private String updator;
}
