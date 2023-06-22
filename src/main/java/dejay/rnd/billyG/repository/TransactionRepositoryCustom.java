package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ChatRoom;
import dejay.rnd.billyG.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepositoryCustom {
    List<Transaction> finds(Long rentalIdx, Long toIdx, boolean cancelYn, Integer[] ownerStatus);

}
