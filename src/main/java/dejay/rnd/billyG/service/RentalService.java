package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.repositoryImpl.RentalRepositories;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalService {
    private final UserRepositories userRepositories;
    private final RentalRepositories rentalRepositories;
    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public void updateViewCnt(Rental rental){
        rental.setViewCnt(rental.getViewCnt()+1);
        rental.setUpdateAt(now_date);
    }

}
