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
public class MainDto {

   private String towns[];

   private Integer status;

   private Integer filter;

   private String keyword;

   private String categories[];

}