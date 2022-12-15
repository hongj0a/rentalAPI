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
@Table(name = "search_keyword")
@Entity
@DynamicInsert
public class Search_Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long search_idx;

    @ManyToOne
    @JoinColumn (name = "USER_IDX")
    private User user;

    @Column
    private String keyword;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

}
