package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Town;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TownRepositories {
    @PersistenceContext
    private final EntityManager em;

    public List<Town> findAllN(Long userIdx) {
        return em.createQuery("select t from Town t where t.deleteYn = false and t.user.userIdx = :userIdx and t.leadTown=false", Town.class)
                .setParameter("userIdx", userIdx)
                .getResultList();
    }

    public List<Town> findAllList(Long userIdx) {
        return em.createQuery("select t from Town t where t.deleteYn = false and t.user.userIdx = :userIdx", Town.class)
                .setParameter("userIdx", userIdx)
                .getResultList();
    }

    public void save(Town town) {
        em.persist(town);
    }

}
