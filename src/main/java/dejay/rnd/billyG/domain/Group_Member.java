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
@Table(name = "group_member")
@Entity
@DynamicInsert
public class Group_Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_member_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "GROUP_IDX")
    private Admin_Group admin_group;

    @Column
    private Integer member_idx;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private String updator;
}
