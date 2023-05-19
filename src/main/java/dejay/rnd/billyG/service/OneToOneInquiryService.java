package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.OneToOneInquiry;
import dejay.rnd.billyG.repository.OneToOneInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OneToOneInquiryService {
    private final OneToOneInquiryRepository oneToOneInquiryRepository;

    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public OneToOneInquiry insertOne(OneToOneInquiry one) {
        return oneToOneInquiryRepository.save(one);
    }

    @Transactional
    public void updateInquiry(OneToOneInquiry one) {
        one.setDeleteAt(now_date);
    }
}
