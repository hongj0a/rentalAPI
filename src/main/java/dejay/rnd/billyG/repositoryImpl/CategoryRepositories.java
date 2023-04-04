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
        return em.createQuery("select c from Category c where c.orderNum not in(9999) and c.categoryType='1' and c.deleteYn=false order by c.orderNum", Category.class)
                .getResultList();
    }

    public List<Category> findBlockTypes(String blockType) {
        return em.createQuery("select c from Category c where c.orderNum not in(9999) and c.categoryType=:blockType and c.deleteYn=false order by c.orderNum", Category.class)
                .setParameter("blockType", blockType)
                .getResultList();
    }

}

