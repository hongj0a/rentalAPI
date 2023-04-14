package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;

@EnableJpaRepositories
@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

    Terms findTopByReservationDateLessThanEqualOrderByReservationDateDesc(Date now_date);
}
