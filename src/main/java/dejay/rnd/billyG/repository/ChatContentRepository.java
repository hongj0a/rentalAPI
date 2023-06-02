package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ChatContent;
import dejay.rnd.billyG.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ChatContentRepository extends JpaRepository<ChatContent, Long> {

    Page<ChatContent> findByChatRoom_chatRoomIdx(Long chatRoomIdx, Pageable pageable);

    List<ChatContent> findByChatRoom_chatRoomIdx(Long chatRoomIdx);
}
