package dejay.rnd.billyG.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dejay.rnd.billyG.domain.QTown;
import dejay.rnd.billyG.domain.Town;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TownRepositories{
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public List<Town> findByTownInfo(ArrayList<Long> towns) {
        QTown town = QTown.town;

        return queryFactory.selectFrom(town)
                .where(town.townIdx.in(towns))
                .fetch();
    }

}
