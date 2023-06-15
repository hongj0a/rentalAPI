package dejay.rnd.billyG.repositoryImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dejay.rnd.billyG.domain.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositories {
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public List<User> findById(String email) {
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
    }
    public List<User> findUsers(Long[] towns){
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();


        if (towns != null && towns.length != 0) {
            builder.and(user.leadTown.in(towns))
                    .or(user.town1.in(towns))
                    .or(user.town2.in(towns))
                    .or(user.town3.in(towns))
                    .or(user.town4.in(towns));
        }

        List<User> results = queryFactory.select(user).distinct().from(user)
                .where((user.activityNoticeYn.eq(true))
                        .and(builder))
                .fetch();
        return results.stream().toList();

    }

    public User findOne(Long userIdx) {
        return em.find(User.class, userIdx);
    }

    public List<User> findByNickName(String nickName){
        return em.createQuery("select u from User u where u.nickName LIKE:nickName and u.status=10", User.class)
                .setParameter("nickName","%"+nickName+"%")
                .getResultList();
    }

}
