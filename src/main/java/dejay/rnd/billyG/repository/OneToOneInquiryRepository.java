package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.OneToOneInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OneToOneInquiryRepository extends JpaRepository<OneToOneInquiry, Long> {
    Page<OneToOneInquiry> findAllByUser_userIdxAndDeleteYnOrderByCreateAtDesc(Long userIdx, boolean deleteYn, Pageable pageable);

    OneToOneInquiry getOne(Long oneIdx);

}
