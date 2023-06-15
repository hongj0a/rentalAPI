package dejay.rnd.billyG.controller;

import com.google.gson.*;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.LoginDto;
import dejay.rnd.billyG.dto.TokenDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthController(UserService userService, TokenProvider tokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * USER 인증 및 인가
     * ROLE
     *  1. 기존회원 로그인, access_token, refresh_token 발급 및 refresh_token db 업데이트
     *  2. 신규회원 회원가입 후, access_token, refresh_token 발급 및 refresh_token db 저장
     *  3. 가입된 email이 있는데, 다른 snsType으로 로그인할 경우 fail
     *  4. ci_value (주민번호기반) 검사 -> 한 사람이 한 계정만 만들 수 있음
     *  5. access_token 12 hour, refresh_token 30 days -- 2023.05.22 정책변경
     *  6. refresh_token expired 시 재 로그인 요청
     *  7. access_token expired 시 refresh_token 으로 검증해서 access_token 재발급, 이 때 AT, RT 모두 갱신
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/authenticate")
    public ResponseEntity<JsonObject> authorize(@RequestBody LoginDto loginDto, HttpServletRequest req) throws java.text.ParseException {
        JsonObject data = new JsonObject();

        //User isUser = userRepository.findByCiValue(loginDto.getCiValue());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String email = loginDto.getEmail();
        String snsType = loginDto.getSnsType();
        User userOne = userRepository.findByEmail(email);

        if (userOne != null) {
            if(userOne.getStatus() == 30) {
                apiRes.setError(ErrCode.err_api_is_delete_user.code());
                apiRes.setMessage(ErrCode.err_api_is_delete_user.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            } else if ( !userOne.getSnsName().equals( loginDto.getSnsType()) ) {
                apiRes.setError(ErrCode.err_api_is_exist_user.code());
                apiRes.setMessage(ErrCode.err_api_is_exist_user.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            }
        } else {
            // ciValue가 없어서 신규회원이면 가입하고 로그인

            UserDto userDto = new UserDto();
            userDto.setEmail(email);
            userDto.setSnsType(snsType);
            userDto.setCiValue(loginDto.getCiValue());
            userDto.setName(loginDto.getName());
            userDto.setPhoneNumber(loginDto.getPhoneNumber());
            userService.signup(userDto);

            userOne = userRepository.findByEmail(email);
        }

        //1년이상 장기 미이용 고객 return 커스텀
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        cal.add(Calendar.YEAR, -1);

        Date date1 = dateFormat.parse(dateFormat.format(cal.getTime()));
        Date date2 = userOne.getLastLoginDate();

        if (date2.before(date1)) {
            apiRes.setError(ErrCode.err_long_time_no_use_user.code());
            apiRes.setMessage(ErrCode.err_long_time_no_use_user.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

        
        //로그인
        TokenDto tokenDto = userService.login(email, snsType);
        data.addProperty("grantType", tokenDto.getGrantType());
        data.addProperty("accessToken",tokenDto.getAccessToken());
        data.addProperty("refreshToken", tokenDto.getRefreshToken());
        data.addProperty("userSeq", userOne.getUserIdx());

        data.addProperty("userStatus", userOne.getStatus());

        if (userOne.getNickName() == null || ("").equals(userOne.getNickName())) {
            data.addProperty("isNicknameEmpty", true);
        } else {
            data.addProperty("isNicknameEmpty", false);
        }

        //공지사항알림 플래그
        //마케팅알림 플래그
        //방해금지 플래그, y일 때 시간
        data.addProperty("marketingNoticeType", userOne.isMarketingNoticeYn());
        data.addProperty("noticeNoticeYn", userOne.isNoticeNoticeYn());
        data.addProperty("doNotDisturbTimeYn", userOne.isDoNotDisturbTimeYn());
        if (userOne.isDoNotDisturbTimeYn() == true) {
            data.addProperty("disturbStartHour", userOne.getDoNotDisturbStartHour());
            data.addProperty("disturbStartMinute", userOne.getDoNotDisturbStartMinute());
            data.addProperty("disturbEndHour", userOne.getDoNotDisturbEndHour());
            data.addProperty("disturbEndMinute", userOne.getDoNotDisturbEndMinute());
        }
        //user town's Information empty check
        //lead_town값 필수, 관심동네 선택
        if (userOne.getLeadTown() == null || ("").equals(userOne.getLeadTown())) {
            data.addProperty("isLeadTownEmpty", true);
        } else {
            data.addProperty("isLeadTownEmpty", false);
        }

        data.addProperty("imageUrl", "http://192.168.1.242:8080/image/");
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/isExistCheck")
    public ResponseEntity<JsonObject> isExistCheck(HttpServletRequest req, @RequestBody LoginDto loginDto) throws java.text.ParseException {
        JsonObject data = new JsonObject();
        User findUser = userRepository.findByEmailAndSnsName(loginDto.getEmail(), loginDto.getSnsType());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        if (findUser != null) {
            //y
            if (findUser.getStatus() == 30) {
                apiRes.setError(ErrCode.err_api_is_delete_user.code());
                apiRes.setMessage(ErrCode.err_api_is_delete_user.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            } else {
                //1년이상 장기 미이용 고객 return 커스텀
                Calendar cal = Calendar.getInstance();
                Calendar cal30 = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

                cal.add(Calendar.YEAR, -1);
                cal30.add(Calendar.MONTH, -1);

                Date date1 = dateFormat.parse(dateFormat.format(cal.getTime()));
                Date date2 = findUser.getLastLoginDate();
                Date date3 = dateFormat.parse(dateFormat.format(cal30.getTime()));

                if (date2.before(date1)) {
                    apiRes.setError(ErrCode.err_long_time_no_use_user.code());
                    apiRes.setMessage(ErrCode.err_long_time_no_use_user.msg());
                    return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
                }

                if (date2.before(date3)) {
                    apiRes.setError(ErrCode.err_30days_no_use_user.code());
                    apiRes.setMessage(ErrCode.err_30days_no_use_user.msg());
                    return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
                }
                apiRes.setError(ErrCode.err_api_is_exist_user.code());
                apiRes.setMessage(ErrCode.err_api_is_exist_user.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            }

        } else {
            //n
            apiRes.setError(ErrCode.err_api_is_new_user.code());
            apiRes.setMessage(ErrCode.err_api_is_new_user.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
    }

    @PostMapping("/refreshTokenValidation")
    public ResponseEntity<JsonObject> refreshTokenValidation(HttpServletRequest req, @RequestBody TokenDto token) throws ParseException {
        JsonObject data = new JsonObject();

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        //유효한 토큰인지 확인
        //유효하다면 true. 만료됐거나 유효하지 않은 토큰이면 false.

        boolean tokenFlag = tokenProvider.validateToken(token.getRefreshToken());

        if (tokenFlag == false) {
            apiRes.setError(ErrCode.err_api_not_found_token.code());
            apiRes.setMessage(ErrCode.err_api_not_found_token.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

        String userEmail = UserMiningUtil.getUserInfo(token.getRefreshToken());
        User findUser = userRepository.findByEmail(userEmail);

        if (findUser != null) {
            if (findUser.getRefreshToken().equals(token.getRefreshToken())) {
                TokenDto tokenDto = userService.login(findUser.getEmail(), findUser.getSnsName());
                data.addProperty("grantType", tokenDto.getGrantType());
                data.addProperty("accessToken",tokenDto.getAccessToken());
                data.addProperty("refreshToken", tokenDto.getRefreshToken());
                data.addProperty("userSeq", findUser.getUserIdx());

                data.addProperty("userStatus", findUser.getStatus());

                if (findUser.getNickName() == null || ("").equals(findUser.getNickName())) {
                    data.addProperty("isNicknameEmpty", true);
                } else {
                    data.addProperty("isNicknameEmpty", false);
                }

                if (findUser.getLeadTown() == null || ("").equals(findUser.getLeadTown())) {
                    data.addProperty("isLeadTownEmpty", true);
                } else {
                    data.addProperty("isLeadTownEmpty", false);
                }
                data.addProperty("imageUrl", "http://192.168.1.242:8080/image/");

                //공지사항알림 플래그
                //마케팅알림 플래그
                //방해금지 플래그, y일 때 시간
                data.addProperty("marketingNoticeType", findUser.isMarketingNoticeYn());
                data.addProperty("noticeNoticeYn", findUser.isNoticeNoticeYn());
                data.addProperty("doNotDisturbTimeYn", findUser.isDoNotDisturbTimeYn());
                if (findUser.isDoNotDisturbTimeYn() == true) {
                    data.addProperty("disturbStartHour", findUser.getDoNotDisturbStartHour());
                    data.addProperty("disturbStartMinute", findUser.getDoNotDisturbStartMinute());
                    data.addProperty("disturbEndHour", findUser.getDoNotDisturbEndHour());
                    data.addProperty("disturbEndMinute", findUser.getDoNotDisturbEndMinute());
                }
            } else {
                apiRes.setError(ErrCode.err_api_not_found_token.code());
                apiRes.setMessage(ErrCode.err_api_not_found_token.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            }
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/ciValueCheck")
    public ResponseEntity<JsonObject> civalueCheck(HttpServletRequest req, @RequestBody UserDto userDto) {
        JsonObject data = new JsonObject();

        User isUser = userRepository.findByCiValue(userDto.getCiValue());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        if (isUser != null) {
            if(isUser.getStatus() == 30) {
                apiRes.setError(ErrCode.err_api_is_delete_user.code());
                apiRes.setMessage(ErrCode.err_api_is_delete_user.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            } else {
                apiRes.setError(ErrCode.err_api_is_exist_user.code());
                apiRes.setMessage(ErrCode.err_api_is_exist_user.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            }
        } else {
            apiRes.setError(ErrCode.err_api_is_new_user.code());
            apiRes.setMessage(ErrCode.err_api_is_new_user.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
    }


}
