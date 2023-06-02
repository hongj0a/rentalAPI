package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface ChatRepository extends JpaRepository<ChatRoom, Long> {

    Page<ChatRoom> findByFromUser_userIdxOrToUser_userIdxAndVisibleToNotInOrUpdatorIsNotNullOrderByUpdateAtDesc(Long fromIdx, Long toIdx, Long[] visibleTo, Pageable pageable);

    List<ChatRoom> findByFromUser_userIdxOrToUser_userIdxAndVisibleToNotInOrUpdatorIsNotNull(Long fromIdx, Long toIdx, Long[] visibleTo);

    ChatRoom findByFromUser_userIdxAndVisibleToNotInAndRental_rentalIdxAndUpdatorIsNotNull(Long fromIdx, Long[] visibleTo, Long rentalIdx);

    ChatRoom findByRental_rentalIdxAndVisibleToIn(Long rentalIdx, Long[] visibleTo);
    ChatRoom getOne(Long chatRoomIdx);
}
