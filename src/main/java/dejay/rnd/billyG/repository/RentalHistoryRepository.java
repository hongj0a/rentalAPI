package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {

}