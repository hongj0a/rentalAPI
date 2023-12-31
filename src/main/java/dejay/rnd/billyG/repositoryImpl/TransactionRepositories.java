package dejay.rnd.billyG.repositoryImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.repository.ChatRoomRepositoryCustom;
import dejay.rnd.billyG.repository.TransactionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepositories implements TransactionRepositoryCustom {
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    QTransaction transaction = QTransaction.transaction;
    QBlockReview blockReview = QBlockReview.blockReview;


    @Override
    public List<Transaction> finds(Long rentalIdx, Long toIdx, boolean cancelYn, Integer[] ownerStatus){

        List<Transaction> result = queryFactory.select(transaction).distinct().from(transaction)
                .where(
                        (transaction.user.userIdx.eq(toIdx)
                                .or(transaction.rental.user.userIdx.eq(toIdx)))
                                .and(transaction.rental.rentalIdx.eq(rentalIdx))
                                .and(transaction.cancelYn.eq(cancelYn))
                                .and(transaction.ownerStatus.notIn(ownerStatus)))
                                .fetch();

        return result.stream().toList();
    }


    @Override
    public List<Transaction> finds2(Long rentalIdx, Long toIdx, boolean cancelYn, Integer[] ownerStatus){

        List<Transaction> result = queryFactory.select(transaction).distinct().from(transaction)
                .where(
                        (transaction.user.userIdx.notIn(toIdx)
                                .and(transaction.rental.user.userIdx.notIn(toIdx)))
                                .and(transaction.rental.rentalIdx.eq(rentalIdx))
                                .and(transaction.cancelYn.eq(cancelYn))
                                .and(transaction.ownerStatus.notIn(ownerStatus)))
                .fetch();

        return result.stream().toList();
    }


}

