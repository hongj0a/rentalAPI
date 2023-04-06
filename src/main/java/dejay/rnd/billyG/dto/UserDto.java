package dejay.rnd.billyG.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dejay.rnd.billyG.domain.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
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
   private String accessToken;

   @Size (max = 1000)
   private String refreshToken;

   @Size(min = 3, max = 100)
   private String leadTownName;

   @Size(min = 3, max = 1000)
   private String ciValue;

   @Size(min = 3, max = 1000)
   private String phoneNumber;

   @Size(min = 3, max = 100)
   private String name;

   @Size(min = 3, max = 100)
   private String towns[];

   private Set<GradeDto> authorityDtoSet;

   public static UserDto from(User user) {
      System.out.println("!@#!@#!@#!@#user.toString() = " + user.toString());
      if(user == null) return null;

      return UserDto.builder()
              .email(user.getEmail())
              .nickName(user.getNickName())
              .phoneNumber(user.getPhoneNum())
              .authorityDtoSet(user.getGrades().stream()
                      .map(authority -> GradeDto.builder().gradeIdx(authority.getGradeIdx()).build())
                      .collect(Collectors.toSet()))
              .build();
   }
}