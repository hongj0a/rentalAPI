package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Likes;
import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.LikeRepository;
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
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional
    public void insertLikeInfo(Rental rental, User user){

        Likes likes = new Likes();
        likes.setRental(rental);
        likes.setUser(user);
        likes.setCreateAt(FrontUtil.getNowDate());

        likeRepository.save(likes);

    }

    @Transactional
    public void updateLikeInfo(Likes likes, User user) {
        likes.setDeleteYn(false);
        likes.setUpdator(user.getEmail());
    }

    @Transactional
    public void removeLikeInfo(Likes likes){

        likes.setDeleteYn(true);
        likes.setDeleteAt(FrontUtil.getNowDate());

    }
}
