package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.UserCount;
import dejay.rnd.billyG.repository.UserCountRepository;
import dejay.rnd.billyG.util.FrontUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCountService {

   private UserCountRepository userCountRepository;

    @Transactional
    public void updateCnt(UserCount userCount) {
        userCount.setUpdateAt(FrontUtil.getNowDate());

    }
}
