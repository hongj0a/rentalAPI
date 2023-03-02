package dejay.rnd.billyG.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainDto {

   private String towns[];

   private Integer status;

   private Integer filter;

   private String keyword;

   private String categories[];

}