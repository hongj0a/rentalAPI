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
@Table(name = "admin_group")
@Entity
@DynamicInsert
public class Admin_Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    @Column
    private String updator;

    @Column
    @NotNull(message = " group name은 Null 일 수 없습니다. ")
    private String group_name;

    @Column(length = 1000)
    private String group_dasc;

    @ColumnDefault("1")
    private boolean active_yn;

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
