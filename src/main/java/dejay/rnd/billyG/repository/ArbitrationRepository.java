package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ArbitrationManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArbitrationRepository extends JpaRepository<ArbitrationManagement, Long> {

    Page<ArbitrationManagement> findAllByUser_userIdxAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean deleteYn, Pageable pageable);
    List<ArbitrationManagement> findAllByUser_userIdxAndDeleteYn(Long userIdx, boolean deleteYn);

    ArbitrationManagement getOne(Long amIdx);

}