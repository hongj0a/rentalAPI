package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.RentalTownInfo;
import dejay.rnd.billyG.domain.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface TownInfoRepository extends JpaRepository<RentalTownInfo, Long> {

    List<RentalTownInfo> findAllByRental_rentalIdx(Long rental_idx);

}
