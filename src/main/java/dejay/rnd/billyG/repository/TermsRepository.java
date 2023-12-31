package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

    List<Terms> findByReservationDateLessThanEqualAndDeleteYnAndTypeOrderByCreateAtDesc(Date reservationDate, boolean deleteYn, Integer type);
}