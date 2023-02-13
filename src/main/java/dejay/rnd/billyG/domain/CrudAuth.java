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
@Table(name = "crud_auth")
@Entity
@DynamicInsert
public class CrudAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "crud_idx")
    private Long crudIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "menuIdx")
    private Menu menu;

    @NotNull
    @Column (name = "menu_url")
    private String menuUrl;

    @Column
    @NotNull
    private Integer auth;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column (name = "createAt")
    private Date create_at;

}
