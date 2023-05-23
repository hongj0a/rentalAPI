package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.BellSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface BellScheduleRepositry extends JpaRepository<BellSchedule, Long> {
}
