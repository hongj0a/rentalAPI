package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Answer;
import dejay.rnd.billyG.domain.OneToOneInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Answer findByOneToOneInquiry_OneIdx(Long one_idx);
}
