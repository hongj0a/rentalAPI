package dejay.rnd.billyG.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grade")
@Entity
@DynamicInsert
public class Grade {

    @Id
    @Column(name = "grade_idx", length = 50)
    private Long gradeIdx;

    @Column(name = "grade_name", length = 50)
    private String gradeName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "grade_score")
    private String gradeScore;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "createAt")
    private Date create_at;

    @Column(name = "update_at")
    private Date updateAt;

    @Column
    private String updator;

    @Column(name = "active_yn")
    private boolean activeYn = true;

    @Column(name = "admin_idx")
    private Long adminIdx;

    @Column(length = 1000)
    private String imageUrl;

}
