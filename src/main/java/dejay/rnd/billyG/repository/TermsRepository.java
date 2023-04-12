package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Review;
import dejay.rnd.billyG.domain.Terms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

    //Terms findFirst();
}
