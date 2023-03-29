package dejay.rnd.billyG.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
   private int reportFlag;
   private Long blockTypeIdx;
   private Long rentalIdx;
   private Long userIdx;
   private Long reviewIdx;
   private String blockContent;
}