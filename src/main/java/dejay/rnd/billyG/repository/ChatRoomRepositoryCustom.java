package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepositoryCustom {

    Page<ChatRoom> findAll(Long fromIdx, Long toIdx, Long[] visibleTo, Pageable pageable);
}
