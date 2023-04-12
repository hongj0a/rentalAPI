package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.OneToOneInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OneToOneInquiryRepository extends JpaRepository<OneToOneInquiry, Long> {
    Page<OneToOneInquiry> findAllByUser_userIdxAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean deleteYn, Pageable pageable);

    List<OneToOneInquiry> findAllByUser_userIdxAndDeleteYn(Long userIdx, boolean deleteYn);
    OneToOneInquiry getOne(Long oneIdx);

}
