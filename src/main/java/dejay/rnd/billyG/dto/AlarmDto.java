package dejay.rnd.billyG.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDto {
    private boolean chatNoticeYn;
    private int marketingNoticeType;
    private boolean activityNoticeYn;
    private boolean doNotDisturbTimeYn;
    private boolean isAfterNoon;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
}
