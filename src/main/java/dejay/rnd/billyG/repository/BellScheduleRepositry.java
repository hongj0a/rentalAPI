package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.BellSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface BellScheduleRepositry extends JpaRepository<BellSchedule, Long> {
    BellSchedule findByUser_userIdxAndRental_rentalIdxAndDeleteYn(Long userIdx, Long rentalIdx, boolean deleteYn);

    List<BellSchedule> findAllByRental_rentalIdxAndDeleteYn(Long rentalIdx, boolean deleteYn);
}
