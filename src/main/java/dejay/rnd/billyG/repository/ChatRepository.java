package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ChatRepository extends JpaRepository<ChatRoom, Long> {

}
