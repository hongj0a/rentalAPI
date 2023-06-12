package dejay.rnd.billyG.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

   private String email;

   private String snsType;

   private String ciValue;

   private String name;

   private String phoneNumber;


}
