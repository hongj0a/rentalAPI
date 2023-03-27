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
public class UserRepositories {
    @PersistenceContext
    private final EntityManager em;

    public List<User> findById(String email) {
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
    }

    public User findOne(Long userIdx) {
        return em.find(User.class, userIdx);
    }

    public List<User> findByNickName(String nickName){
        return em.createQuery("select u from User u where u.nickName LIKE:nickName and u.deleteYn=false and u.activeYn=true", User.class)
                .setParameter("nickName","%"+nickName+"%")
                .getResultList();
    }

}
