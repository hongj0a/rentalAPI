package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ChatContent;
import dejay.rnd.billyG.domain.ChatImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {

    List<ChatImage> findByChatContent_ChatIdx(Long chatContentIdx);
}
