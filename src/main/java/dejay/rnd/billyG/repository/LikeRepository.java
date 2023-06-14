package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Likes;
import dejay.rnd.billyG.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    Likes findByRental_rentalIdxAndUser_userIdxAndDeleteYn(Long rentalIdx, Long userIdx, boolean deleteYn);
    Likes findByRental_rentalIdxAndUser_userIdx(Long rentalIdx, Long userIdx);

    List<Likes> findAllByRental_rentalIdxAndDeleteYn(Long rentalIdx, boolean deleteYn);
    Page<Likes> findByUser_userIdxAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean deleteYn, Pageable pageable);

    List<Likes> findByUser_userIdxAndDeleteYn(Long userIdx, boolean deleteYn);
}
