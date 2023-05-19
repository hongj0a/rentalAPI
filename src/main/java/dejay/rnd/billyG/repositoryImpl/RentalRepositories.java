package dejay.rnd.billyG.repositoryImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.repository.RentalRepository;
import dejay.rnd.billyG.repository.RentalRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.list;

@Repository
@RequiredArgsConstructor
public class RentalRepositories implements RentalRepositoryCustom {
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final RentalRepository rentalRepository;

    /*@Transactional
    public List<Rental> findByMainAllTest() {
        QRental rental = QRental.rental;
        QRentalCategoryInfo rentalCategoryInfo = QRentalCategoryInfo.rentalCategoryInfo;
        QTransaction transaction = QTransaction.transaction;

        return queryFactory.select(rental)
                .from(rental)
                .leftJoin(rental.rentalCategoryInfos, rentalCategoryInfo)
                .fetchJoin()
                .leftJoin(rental.transactionInfos, transaction)
                .distinct()
                .fetch();

        *//*return queryFactory.select(Projections.fields(Rental.class, new CaseBuilder()
                        .when(rental.status.in(1))
                        .then(1)
                        .when(rental.status.in(2))
                        .then(2)
                        .when(rental.status.in(3))
                        .then(3)
                        .when(rental.status.in(4))
                        .then(4)
                        .otherwise(rental.status).as("status")

                , rental.rentalIdx
                , rental.deposit
                , rental.rentalPrice
                , rental.content
                , rental.tradingMethod
                , rental.likeCnt
                , rental.viewCnt
                , rental.updateAt
                , rental.completeAt
                , rental.activeYn
                , rental.deleteYn
                , rental.deleteAt
                , rental.updator
                , rental.pullUpAt
                , rental.pullUpCnt
                , rental.leadTown
                , rental.town1
                , rental.town2
                , rental.town3
                , rental.town4
                , rental.createAt
                , rental.title
                , rental.user
                , rentalCategoryInfo
                , transaction
                , rental.activeYn))
                .from(rental)
                .leftJoin(rental.rentalCategoryInfos, rentalCategoryInfo)
                .leftJoin(rental.transactionInfos, transaction)
                .distinct()
                .fetch();*//*
    }*/

    @Override
    public Page<Rental> findAll(ArrayList<Integer> status, Integer filter, String title, Long[] towns, Long[] categories, Pageable pageable){
        QRental rental = QRental.rental;
        QRentalCategoryInfo rentalCategoryInfo = QRentalCategoryInfo.rentalCategoryInfo;

        BooleanBuilder builder = new BooleanBuilder();

        if (categories != null && categories.length != 0) {
            builder.and(rentalCategoryInfo.category.categoryIdx.in(categories));
        }

        if (towns != null && towns.length != 0) {
            builder.and(rental.leadTown.in(towns))
                    .or(rental.town1.in(towns))
                    .or(rental.town2.in(towns))
                    .or(rental.town3.in(towns))
                    .or(rental.town4.in(towns));
        }


        //인기순
        if (filter == 1) {
            List<Rental> results = queryFactory.select(rental).distinct().from(rental)
                    .join(rental.rentalCategoryInfos, rentalCategoryInfo).fetchJoin()
                    .where((rental.status.in(status)).and(rental.title.contains(title))
                            .and(builder)
                            .and(rental.activeYn.eq(true))
                            .and(rental.deleteYn.eq(false)))
                    .orderBy(rental.likeCnt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            List<Rental> content = results.stream().toList();
            return new PageImpl<>(content, pageable, content.size());
        }

        //최신순
        if (filter == 0) {
            List<Rental> results = queryFactory.select(rental).distinct().from(rental)
                    .join(rental.rentalCategoryInfos, rentalCategoryInfo).fetchJoin()
                    .where((rental.status.in(status)).and(rental.title.contains(title))
                    .and(builder)
                    .and(rental.activeYn.eq(true))
                    .and(rental.deleteYn.eq(false)))
                    .orderBy(rental.pullUpAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            List<Rental> content = results.stream().toList();
            return new PageImpl<>(content, pageable, content.size());
        }

        return null;
    }

    public Integer getTotalCount(ArrayList<Integer> status, String title, Long[] towns, Long[] categories) {

        QRental rental = QRental.rental;
        QRentalCategoryInfo rentalCategoryInfo = QRentalCategoryInfo.rentalCategoryInfo;

        BooleanBuilder builder = new BooleanBuilder();

        if (categories != null && categories.length != 0) {
            builder.and(rentalCategoryInfo.category.categoryIdx.in(categories));
        }

        if (towns != null && towns.length != 0) {
            builder.and(rental.leadTown.in(towns))
                    .or(rental.town1.in(towns))
                    .or(rental.town2.in(towns))
                    .or(rental.town3.in(towns))
                    .or(rental.town4.in(towns));
        }
        //0 처리..
        List<Long> totalInfo = queryFactory.select(rental.rentalIdx).distinct().from(rental)
                .join(rental.rentalCategoryInfos, rentalCategoryInfo)
                .where((rental.status.in(status)).and(rental.title.contains(title))
                        .and(builder)
                        .and(rental.activeYn.eq(true))
                        .and(rental.deleteYn.eq(false)))
                .fetch();
        Integer total = totalInfo.size();
        return total;
    }


}

