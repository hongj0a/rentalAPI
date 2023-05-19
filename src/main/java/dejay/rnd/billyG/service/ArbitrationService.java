package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.ArbitrationManagement;
import dejay.rnd.billyG.domain.Review;
import dejay.rnd.billyG.repository.ArbitrationRepository;
import dejay.rnd.billyG.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArbitrationService {
    private final ArbitrationRepository arbitrationRepository;
    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public void deleteRM(ArbitrationManagement am) {
        am.setUpdateAt(now_date);
    }


}
