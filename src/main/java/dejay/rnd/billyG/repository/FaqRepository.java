package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
  //Page<Faq> findByFaqTypeAndDeleteYnAndActiveYn(FaqType faqTypeIdx, Boolean deleteYn, Boolean activeYn, Pageable pageable);

    Page<Faq> findAllByCategory_categoryIdxInAndActiveYnOrderByCreateAtDesc(ArrayList<Long> categoryIdx, boolean active_yn, Pageable pageable);
    List<Faq> findAllByCategory_categoryIdxInAndActiveYn(ArrayList<Long> categoryIdx, boolean active_yn);
}