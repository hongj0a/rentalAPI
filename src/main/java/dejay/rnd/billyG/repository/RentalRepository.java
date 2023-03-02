package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.RentalImage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @EntityGraph(attributePaths = {"rentalTownInfos"})
    List<Rental> findAllByStatusAndTitleContainingOrderByCreateAtDesc(Integer Status, String title);

    @EntityGraph(attributePaths = {"rentalTownInfos"})
    List<Rental> findAllByStatusAndTitleContainingOrderByLikeCntDesc(Integer Status, String title);

    @EntityGraph(attributePaths = {"rentalTownInfos"})
    List<Rental> findAllByTitleContainingOrderByCreateAtDesc(String keyword);

    @EntityGraph(attributePaths = {"rentalTownInfos"})
    List<Rental> findAllByTitleContainingOrderByLikeCntDesc(String keyword);

}
