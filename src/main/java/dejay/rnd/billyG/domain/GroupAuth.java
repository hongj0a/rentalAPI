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
@Table(name = "group_auth")
@Entity
@DynamicInsert
public class GroupAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "group_auth_idx")
    private Long groupAuthIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "groupIdx")
    private AdminGroup adminGroup;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "menuIdx")
    private Menu menu;

    @ColumnDefault("0")
    @Column (name = "read_auth")
    private boolean readAuth;

    @ColumnDefault("0")
    @Column (name = "write_auth")
    private boolean writeAuth;

    @ColumnDefault("0")
    @Column (name = "update_auth")
    private boolean updateAuth;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "createAt")
    private Date create_at;

    @Column (name = "update_at")
    private Date updateAt;

    @Column
    private String updator;
}
