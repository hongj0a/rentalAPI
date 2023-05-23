package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.ChatRoom;
import dejay.rnd.billyG.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRoomRepository;
    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public ChatRoom createChat(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }
}
