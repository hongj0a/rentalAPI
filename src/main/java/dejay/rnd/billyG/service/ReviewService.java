package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Review;
import dejay.rnd.billyG.repository.ReviewRepository;
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
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional
    public Review insertReview(Review review) {
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Review review) {
        review.setUpdateAt(FrontUtil.getNowDate());
    }


}
