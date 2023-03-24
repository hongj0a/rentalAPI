package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    /*@EntityGraph(attributePaths = {"rentalTownInfos", "rentalCategoryInfos"})
    Page<Rental> findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndStatusAndTitleContainingOrderByCreateAtDesc(Long[] categories, Long[] towns, Integer Status, String title, Pageable pageable);

    @EntityGraph(attributePaths = {"rentalTownInfos", "rentalCategoryInfos"})
    Page<Rental> findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndStatusAndTitleContainingOrderByLikeCntDesc(Long[] categories, Long[] towns, Integer Status, String title, Pageable pageable);

    @EntityGraph(attributePaths = {"rentalTownInfos", "rentalCategoryInfos"})
    Page<Rental> findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndTitleContainingOrderByCreateAtDesc(Long[] categories, Long[] towns, String title, Pageable pageable);

    @EntityGraph(attributePaths = {"rentalTownInfos", "rentalCategoryInfos"})
    Page<Rental> findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndTitleContainingOrderByLikeCntDesc(Long[] categories, Long[] towns, String title, Pageable pageable);

*/
}
