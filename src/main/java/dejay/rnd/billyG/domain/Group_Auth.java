package dejay.rnd.billyG.domain;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
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
public class Group_Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_auth_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "GROUP_IDX")
    private Admin_Group admin_group;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "menu_idx")
    private Menu menu;

    @ColumnDefault("0")
    private boolean read_auth;

    @ColumnDefault("0")
    private boolean write_auth;

    @ColumnDefault("0")
    private boolean update_auth;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private String updator;
}
