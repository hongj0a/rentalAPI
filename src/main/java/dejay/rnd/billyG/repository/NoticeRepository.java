package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Pageable> {
    // findAll japRepository 기본 기능이기 때문에 굳이 선언해 주지 않아도 됨.

    Page<Notice> findByDeleteYn(Boolean deleteYn,Pageable pageable);
}
