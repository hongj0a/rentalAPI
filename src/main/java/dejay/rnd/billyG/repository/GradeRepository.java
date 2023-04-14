package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Alarm;
import dejay.rnd.billyG.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Grade findTop1ByOrderByGradeScoreDesc();

    Grade getOne(Long gradeIdx);

    List<Grade> findByActiveYnAndMenuNumNotIn(boolean activeYn, int[] menuNum);

}
