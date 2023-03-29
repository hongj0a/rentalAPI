package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByHostIdxAndDeleteYnAndCreateAtGreaterThanEqualAndCreateAtLessThanEqualOrderByCreateAtDesc(Long userIdx, boolean deleteYn, LocalTime beforeDate, LocalTime currentDate);
}
