package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.InquiryImage;
import dejay.rnd.billyG.domain.RentalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface InquiryImageRepository extends JpaRepository<InquiryImage, Long> {

    List<InquiryImage> findByOneToOneInquiry_oneIdx(Long oneIdx);
}
