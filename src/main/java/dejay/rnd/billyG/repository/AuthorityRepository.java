package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Grade, String> {
}
