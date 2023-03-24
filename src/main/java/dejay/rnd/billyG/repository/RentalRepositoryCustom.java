package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RentalRepositoryCustom {
    Page<Rental> findAll(ArrayList<Integer> status, Integer filter, String title, Long[] towns, Long[] categories, Pageable pageable);
}
