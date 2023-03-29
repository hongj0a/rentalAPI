package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByRental_rentalIdxAndOwnerStatusOrderByCreateAtDesc(Long rentalIdx, Integer ownerStatus);
    Page<Transaction> findByRental_rentalIdxAndOwnerStatusOrderByCreateAtDesc(Long rentalIdx, Integer ownerStatus, Pageable pageable);
}
