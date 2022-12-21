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
@Table(name = "admin_log")
@Entity
@DynamicInsert
public class Admin_Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long log_idx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;

    @Column
    private String content;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

}
