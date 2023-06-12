package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.ChatContent;
import dejay.rnd.billyG.domain.ChatRoom;
import dejay.rnd.billyG.repository.ChatContentRepository;
import dejay.rnd.billyG.repository.ChatRepository;
import dejay.rnd.billyG.util.FrontUtil;
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
        chatRoom.setExitAt(FrontUtil.getNowDate());
        chatRoom.setUpdateAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void updateChatRoom(ChatRoom chatRoom) {
        chatRoom.setUpdateAt(FrontUtil.getNowDate());
    }
}
