package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Likes;
import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.ToBlock;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.LikeRepository;
import dejay.rnd.billyG.repository.ToBlockRepository;
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
public class ToBlockService {
    private final ToBlockRepository toBlockRepository;
    @Transactional
    public void insertBlockInfo(ToBlock toBlock){

        toBlock.setCreateAt(FrontUtil.getNowDate());
        toBlockRepository.save(toBlock);

    }

    @Transactional
    public void setBlockInfoUpdate(ToBlock toBlock) {
        toBlock.setDeleteAt(FrontUtil.getNowDate());
    }

}
