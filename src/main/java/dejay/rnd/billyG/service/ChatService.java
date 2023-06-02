package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.ChatContent;
import dejay.rnd.billyG.domain.ChatRoom;
import dejay.rnd.billyG.repository.ChatContentRepository;
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
    private final ChatContentRepository chatContentRepository;
    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public ChatRoom createChat(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatContent insert(ChatContent content) {
        return chatContentRepository.save(content);
    }

    @Transactional
    public void updateExit(ChatRoom chatRoom) {
        chatRoom.setExitAt(now_date);
        chatRoom.setUpdateAt(now_date);
    }

    @Transactional
    public void updateChatRoom(ChatRoom chatRoom) {
        chatRoom.setUpdateAt(now_date);
    }
}
