package dejay.rnd.billyG.dto;

import com.google.gson.JsonArray;
import dejay.rnd.billyG.domain.ChatImage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
   private ArrayList<Map<String,String>> images;
   private Long regDate;
   private String image;
   private String nickName;
   private String status;
   private int step;
   private boolean systemYn;
}