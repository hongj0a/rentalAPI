package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.Transaction;
import org.hibernate.spi.TreatedNavigablePath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction getOne(Long transactionIdx);

    List<Transaction> findByRental_rentalIdxAndOwnerStatusOrderByCreateAtDesc(Long rentalIdx, Integer ownerStatus);
    Page<Transaction> findByRental_rentalIdxAndOwnerStatusOrderByCreateAtDesc(Long rentalIdx, Integer ownerStatus, Pageable pageable);

    //렌탈함 status != all
    Page<Transaction> findByRental_User_userIdxAndCancelYnAndOwnerStatusInOrderByCreateAtDesc(Long userIdx, boolean cancelYn, int[] ownerStatus, Pageable pageable);
    List<Transaction> findByRental_User_userIdxAndCancelYnAndOwnerStatusIn(Long userIdx, boolean cancelYn, int[] ownerStatus);

    //렌탈오너, status == all
    Page<Transaction> findByRental_User_userIdxAndCancelYnOrderByCreateAtDesc(Long userIdx, boolean cancelYn, Pageable pageable);
    List<Transaction> findByRental_User_userIdxAndCancelYn(Long userIdx, boolean cancelYn);

    //렌탈러. status != all
    Page<Transaction> findByUser_userIdxAndCancelYnAndRenterStatusInOrderByCreateAtDesc(Long userIdx, boolean cancelYn, int[] renterStatus, Pageable pageable);
    List<Transaction> findByUser_userIdxAndCancelYnAndRenterStatusIn(Long userIdx, boolean cancelYn, int[] renterStatus);

    //렌탈러, status == all
    Page<Transaction> findByUser_userIdxAndCancelYnOrderByCreateAtDesc(Long userIdx, boolean cancelYn, Pageable pageable);
    List<Transaction> findByUser_userIdxAndCancelYn(Long userIdx, boolean cancelYn);

    List<Transaction> findByRental_rentalIdxAndUser_userIdxAndOwnerStatusNotIn(Long rentalIdx, Long userIdx, int[] ownerStatus);
}
