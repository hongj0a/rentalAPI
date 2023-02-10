package dejay.rnd.billyG.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dejay.rnd.billyG.domain.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

   @NotNull
   @Size(min = 3, max = 50)
   private String email;

   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
   @NotNull
   @Size(min = 3, max = 100)
   private String snsType;

   @Size(min = 3, max = 100)
   private String snsName;

   @Size(min = 3, max = 50)
   private String nickName;

   @Size (max = 1000)
   private String access_token;

   @Size (max = 1000)
   private String refresh_token;

   private Set<GradeDto> authorityDtoSet;

   public static UserDto from(User user) {
      if(user == null) return null;

      return UserDto.builder()
              .email(user.getEmail())
              .nickName(user.getNickName())
              .authorityDtoSet(user.getGrades().stream()
                      .map(authority -> GradeDto.builder().gradeName(authority.getGradeName()).build())
                      .collect(Collectors.toSet()))
              .build();
   }
}