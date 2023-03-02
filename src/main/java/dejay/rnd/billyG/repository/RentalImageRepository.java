package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.RentalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface RentalImageRepository extends JpaRepository<RentalImage, Long> {

    List<RentalImage> findByRental_rentalIdx(Long rental_idx);
}
