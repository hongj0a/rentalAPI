package dejay.rnd.billyG.repositoryImpl;

import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.domain.UserCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCountRepositories {
    @PersistenceContext
    private final EntityManager em;

    public void save(UserCount userCount){
        em.persist(userCount);
    }
}
