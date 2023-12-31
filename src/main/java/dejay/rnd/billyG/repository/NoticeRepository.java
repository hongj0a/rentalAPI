package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice,Pageable> {
    // findAll japRepository 기본 기능이기 때문에 굳이 선언해 주지 않아도 됨.

    Page<Notice> findByDeleteYnAndActiveYnAndActiveAtLessThanEqual(boolean deleteYn, boolean activeYn, Date nowDate, Pageable pageable);
    List<Notice> findAllByDeleteYnAndActiveYnAndActiveAtLessThanEqual(boolean deleteYn, boolean activeYn, Date nowDate);

    Notice findByStartAtLessThanEqualAndEndAtGreaterThanEqualAndViewType(Date startDate, Date endDate, String viewType);
}
