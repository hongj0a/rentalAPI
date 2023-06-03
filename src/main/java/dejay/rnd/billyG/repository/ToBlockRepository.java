package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Notice;
import dejay.rnd.billyG.domain.ToBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToBlockRepository extends JpaRepository<ToBlock, Pageable> {

    List<ToBlock> findByUser_userIdxAndDeleteYn(Long userIdx, boolean deleteYn);

    Page<ToBlock> findByUser_userIdxAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean deleteYn, Pageable pageable);

    ToBlock findByUser_userIdxAndBlockUser_userIdxAndDeleteYn(Long userIdx, Long blockUserIdx, boolean deleteYn);

}
