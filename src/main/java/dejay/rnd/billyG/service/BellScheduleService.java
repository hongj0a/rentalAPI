package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.BellSchedule;
import dejay.rnd.billyG.domain.Likes;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.BellScheduleRepositry;
import dejay.rnd.billyG.repositoryImpl.AlarmRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Transactional
@Service
public class BellScheduleService {
    private final BellScheduleRepositry bellScheduleRepositry;

    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public void update(BellSchedule bellSchedule) {
        bellSchedule.setUpdateAt(now_date);
    }
}
