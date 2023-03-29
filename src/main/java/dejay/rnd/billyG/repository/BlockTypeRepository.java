package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.Alarm;
import dejay.rnd.billyG.domain.BlockType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface BlockTypeRepository extends JpaRepository<BlockType, Long> {

    BlockType getOne(Long blockTypeIdx);
    List<BlockType> findByTypeFlagAndActiveYnAndDeleteYn(int type, boolean activeYn, boolean deleteYn);
}
