package dejay.rnd.billyG.repositoryImpl;

import dejay.rnd.billyG.domain.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositories {

    @PersistenceContext
    private final EntityManager em;


    public List<Category> findAllN() {
        return em.createQuery("select c from Category c where c.activeYn=true and c.deleteYn=false order by c.orderNum", Category.class)
                .getResultList();
    }
}
