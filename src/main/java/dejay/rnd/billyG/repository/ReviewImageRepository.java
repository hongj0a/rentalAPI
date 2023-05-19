package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Review;
import dejay.rnd.billyG.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReview_ReviewIdx(Long reviewIdx);
}
