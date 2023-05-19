package dejay.rnd.billyG.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentalDto {

   private String towns[];

   private String title;

   private String content;

   private String rentalDailyFee;

   private String categories[];


}