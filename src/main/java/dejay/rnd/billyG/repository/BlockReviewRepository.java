package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.BlockReview;
import dejay.rnd.billyG.domain.BlockUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface BlockReviewRepository extends JpaRepository<BlockReview, Long> {

}
