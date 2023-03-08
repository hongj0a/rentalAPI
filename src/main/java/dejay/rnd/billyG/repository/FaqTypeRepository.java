package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.FaqType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqTypeRepository extends JpaRepository<FaqType, Pageable> {
}