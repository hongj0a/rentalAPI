package dejay.rnd.billyG.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GradeDto {
   private Long gradeIdx;
   private String gradeName;
}