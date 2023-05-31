package dejay.rnd.billyG.controller;

import dejay.rnd.billyG.domain.ChatContent;
import dejay.rnd.billyG.domain.ChatRoom;
import dejay.rnd.billyG.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/send")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @MessageMapping("/hello")
    @SendTo("/room/chats")
    public ChatContent chatting(@DestinationVariable Long roomIdx, ChatContent message) {


        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomIdx(roomIdx);


        chatService.createChat(chatRoom);
        return ChatContent.builder()
                .user(chatRoom.getRental().getUser())
                .content(message.getContent())
                .build();
    }

}