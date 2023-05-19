package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.RentalCategoryInfo;
import dejay.rnd.billyG.domain.RentalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface RentalCategoryInfoRepository extends JpaRepository<RentalCategoryInfo, Long> {

    List<RentalCategoryInfo> findByRental_rentalIdx(Long rental_idx);
}
