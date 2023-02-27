package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface TownRepository extends JpaRepository<Town, Long> {

    Town findByLeadTownAndUser_userIdx(boolean leadTown, Long userIdx);

    List<Town> findByUser_userIdx(Long userIdx);
    Town getOne(Long townIdx);
}
