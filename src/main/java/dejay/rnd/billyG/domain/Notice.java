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
@Table(name = "notice")
@Entity
@DynamicInsert
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notice_idx;

    @ManyToOne
    @JoinColumn (name = "ADMIN_IDX")
    private Admin admin;


    /**
     * 추후에 정의 할것
     * 0 : 공지
     * 1 : 채용
     * 2 : 이벤트 ...
     */
    @ColumnDefault("0")
    private Integer type;

    @Column
    private String title;

    @Column
    @Lob
    private String content;

    @ColumnDefault("0")
    private boolean delete_yn;

    @ColumnDefault("1")
    private boolean active_yn;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date create_at;

    @Column
    private Date update_at;

    @Column
    private Date delete_at;

}
