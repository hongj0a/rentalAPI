package dejay.rnd.billyG.dto;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatContentDto {
   private Long messageSeq;
   private Long chatRoomIdx;
   private Long userIdx;
   private Long transactionIdx;
   private String message;
   private Long regDate;
   private String image;
   private String nickName;
   private String status;
   private int step;
   private boolean systemYn;
}