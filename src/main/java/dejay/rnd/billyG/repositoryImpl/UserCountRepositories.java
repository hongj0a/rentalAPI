package dejay.rnd.billyG.repositoryImpl;

import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.domain.UserCount;
import dejay.rnd.billyG.util.FrontUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCountRepositories {
    @PersistenceContext
    private final EntityManager em;

    public void save(UserCount userCount){
        userCount.setCreateAt(FrontUtil.getNowDate());
        em.persist(userCount);
    }
}
