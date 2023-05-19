package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.StatusHistory;
import dejay.rnd.billyG.domain.UserEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface UserEvaluationRepository extends JpaRepository<UserEvaluation, Long> {

}
