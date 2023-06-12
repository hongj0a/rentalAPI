package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}