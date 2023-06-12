package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.repository.RentalRepository;
import dejay.rnd.billyG.repositoryImpl.RentalRepositories;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import dejay.rnd.billyG.util.FrontUtil;
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
    private final RentalRepository rentalRepository;

    @Transactional
    public Rental insertRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    @Transactional
    public void updateRental(Rental rental) {
        rental.setUpdateAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void deleteRental(Rental rental) {
        rental.setDeleteAt(FrontUtil.getNowDate());
    }
    @Transactional
    public void pullUpRental(Rental rental) {
        rental.setPullUpAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void updateViewCnt(Rental rental){
        rental.setViewCnt(rental.getViewCnt()+1);
        rental.setUpdateAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void updateLikeCnt(Rental rental, boolean likeFlag){
        if (likeFlag == true) {
            rental.setLikeCnt(rental.getLikeCnt()+1);
        } else {
            rental.setLikeCnt(rental.getLikeCnt()-1);
        }
        rental.setUpdateAt(FrontUtil.getNowDate());
    }
}
