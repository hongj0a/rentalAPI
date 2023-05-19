package dejay.rnd.billyG.service;

import dejay.rnd.billyG.repository.UserCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCountService {

   private UserCountRepository userCountRepository;

    @Transactional
    public void setUserLoginCount(Long userIdx) {

        //usercount 있으면 업데이트

    }
}
