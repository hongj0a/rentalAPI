package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.BlockPost;
import dejay.rnd.billyG.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface BlockPostRepository extends JpaRepository<BlockPost, Long> {

    BlockPost findByReporterIdxAndRental_rentalIdxAndProcessingStatusNotIn(Long reporterIdx, Long rentalIdx, int[] processingStatus);

}
