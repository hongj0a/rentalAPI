package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Slangs;
import dejay.rnd.billyG.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface SlangsRepository extends JpaRepository<Slangs, Long> {

    List<Slangs> findAllByActiveYn(boolean active_yn);
}
