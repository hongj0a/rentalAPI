package dejay.rnd.billyG.dto;

import dejay.rnd.billyG.domain.ChatRoom;
import dejay.rnd.billyG.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatContentDto {
   private Long chatIdx;
   private Long chatRoomIdx;
   private Long userIdx;
   private String message;

}