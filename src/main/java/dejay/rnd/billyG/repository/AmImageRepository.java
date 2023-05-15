package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.AmImage;
import dejay.rnd.billyG.domain.ArbitrationManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmImageRepository extends JpaRepository<AmImage, Long> {

    List<AmImage> findByArbitrationManagement_AmIdx(Long amIdx);
}