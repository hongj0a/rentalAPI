package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Alarm;
import dejay.rnd.billyG.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category getOne(Long categoryIdx);

    List<Category> findAllByCategoryTypeOrderByOrderNum(String categoryType);
}
