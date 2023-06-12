package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.repository.BlockPostRepository;
import dejay.rnd.billyG.repository.BlockReviewRepository;
import dejay.rnd.billyG.repository.BlockUserRepository;
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
public class ReportService {
    private final BlockPostRepository blockPostRepository;
    private final BlockUserRepository blockUserRepository;
    private final BlockReviewRepository blockReviewRepository;

    @Transactional
    public void insertBlockPost(Rental rental, Category blockType, String reason, Long userIdx){

        BlockPost blockPost = new BlockPost();
        blockPost.setRental(rental);
        blockPost.setCategory(blockType);;
        blockPost.setReason(reason);
        blockPost.setReporterIdx(userIdx);
        blockPost.setCreateAt(FrontUtil.getNowDate());

        blockPostRepository.save(blockPost);

    }

    @Transactional
    public void insertBlockUser(User user, Category blockType, String reason, Long userIdx){

        BlockUser blockUser = new BlockUser();
        blockUser.setUser(user);
        blockUser.setCategory(blockType);;
        blockUser.setReason(reason);
        blockUser.setReporterIdx(userIdx);
        blockUser.setCreateAt(FrontUtil.getNowDate());

        blockUserRepository.save(blockUser);

    }

    @Transactional
    public void insertBlockReview(Review review, Category blockType, String reason, Long userIdx){

        BlockReview blockReview = new BlockReview();
        blockReview.setReview(review);
        blockReview.setCategory(blockType);;
        blockReview.setReason(reason);
        blockReview.setReporterIdx(userIdx);
        blockReview.setCreateAt(FrontUtil.getNowDate());

        blockReviewRepository.save(blockReview);

    }

}
