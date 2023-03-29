package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.BlockPost;
import dejay.rnd.billyG.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface BlockPostRepository extends JpaRepository<BlockPost, Long> {

}
