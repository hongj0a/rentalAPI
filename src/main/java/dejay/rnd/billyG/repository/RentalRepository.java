package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Rental> findByUser_userIdxAndActiveYnAndDeleteYn(Long userIdx, boolean active_yn, boolean delete_yn);
    Rental getOne(Long rentalIdx);

    Page<Rental> findByUser_userIdxAndActiveYnAndDeleteYnAndStatusInOrderByCreateAtDesc(Long user_idx, boolean active_yn, boolean delete_yn, ArrayList<Integer> status, Pageable pageable);

    List<Rental> findByUser_userIdxAndActiveYnAndDeleteYnAndStatusIn(Long user_idx, boolean active_yn, boolean delete_yn, ArrayList<Integer> status);
    Page<Rental> findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotInOrderByCreateAtDesc(Long user_idx, boolean active_yn, boolean delete_yn, int[] status, Pageable pageable);
    List<Rental> findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotIn(Long user_idx, boolean active_yn, boolean delete_yn, int[] status);
}
