package dejay.rnd.billyG.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainDto {

   private Long towns[];

   private Integer status;

   private Integer filter;

   private String keyword;

   private Long rentalIdx;

   private Long categories[];


}