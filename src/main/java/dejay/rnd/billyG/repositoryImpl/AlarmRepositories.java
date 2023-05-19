package dejay.rnd.billyG.repositoryImpl;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dejay.rnd.billyG.domain.Alarm;
import dejay.rnd.billyG.domain.QAlarm;
import dejay.rnd.billyG.domain.QTown;
import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.service.AlarmService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlarmRepositories {
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    QAlarm qAlarm = QAlarm.alarm;

    public List<Town> findByTownInfo(ArrayList<Long> towns) {
        QTown town = QTown.town;

        return queryFactory.selectFrom(town)
                .where(town.townIdx.in(towns))
                .fetch();
    }


}
