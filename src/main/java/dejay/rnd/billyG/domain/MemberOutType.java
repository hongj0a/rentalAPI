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
@Table(name = "member_out_type")
@Entity
@DynamicInsert
public class MemberOutType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "out_idx")
    private Long outIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "adminIdx")
    private Admin admin;

    @Column
    @NotNull
    private String type;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private boolean deleteYn;

    @Column
    private String updator;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "create_at")
    private Date createAt;

    @Column (name = "update_at")
    private Date updateAt;

    @Column (name = "delete_at")
    private Date deleteAt;


}
