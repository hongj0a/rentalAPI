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
                    .orderBy(rental.viewCnt.desc())
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

    public List<Rental> getTotalCount(ArrayList<Integer> status, String title, Long[] towns, Long[] categories) {

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
        List<Rental> totalInfo = queryFactory.select(rental).distinct().from(rental)
                .join(rental.rentalCategoryInfos, rentalCategoryInfo)
                .where((rental.status.in(status)).and(rental.title.contains(title))
                        .and(builder)
                        .and(rental.activeYn.eq(true))
                        .and(rental.deleteYn.eq(false)))
                .fetch();
        //Integer total = totalInfo.size();
        return totalInfo.stream().toList();
    }



}

