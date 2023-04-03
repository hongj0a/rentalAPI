package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Likes;
import dejay.rnd.billyG.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    Likes findByRental_rentalIdxAndUser_userIdxAndDeleteYn(Long rentalIdx, Long userIdx, boolean deleteYn);
    Likes findByRental_rentalIdxAndUser_userIdx(Long rentalIdx, Long userIdx);
}
