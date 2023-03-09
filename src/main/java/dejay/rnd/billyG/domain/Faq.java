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
@Table(name = "faq")
@Entity
@DynamicInsert
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "faq_idx")
    private Long faqIdx;

    @ManyToOne
    @NotNull
    @JoinColumn (name = "faqTypeIdx")
    private FaqType faqType;

    @Column
    private String title;

    @Column
    @Lob
    private String content;

    @ColumnDefault("0")
    @Column (name = "delete_yn")
    private Boolean deleteYn;

    @ColumnDefault("1")
    @Column (name = "active_yn")
    private Boolean activeYn;

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
