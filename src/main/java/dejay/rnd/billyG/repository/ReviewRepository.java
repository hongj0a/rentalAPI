package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review getOne(Long reviewIdx);

    Review findByTransaction_TransactionIdxAndTransaction_OwnerStatus(Long trIdx, int ownerStatus);
    List<Review> findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean active_yn, boolean delete_yn);
    Page<Review> findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean active_yn, boolean delete_yn, Pageable pageable);

    Page<Review> findByRenterIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean active_yn, boolean delete_yn, Pageable pageable);
    List<Review> findByRenterIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean active_yn, boolean delete_yn);

}
