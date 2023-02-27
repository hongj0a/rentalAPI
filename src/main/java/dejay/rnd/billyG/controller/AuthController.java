package dejay.rnd.billyG.controller;

import com.google.gson.*;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.LoginDto;
import dejay.rnd.billyG.dto.TokenDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;
    private final TownService townService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthController(UserService userService, TownService townService, TokenProvider tokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.townService = townService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * USER 인증 및 인가
     * ROLE
     *  1. 기존회원 로그인, access_token, refresh_token 발급 및 refresh_token db 업데이트
     *  2. 신규회원 회원가입 후, access_token, refresh_token 발급 및 refresh_token db 저장
     *  3. 가입된 email이 있는데, 다른 snsType으로 로그인할 경우, snsType updqte 후 토큰 발급
     */
    @PostMapping("/authenticate")
    public ResponseEntity<JsonObject> authorize(@RequestBody LoginDto loginDto, HttpServletRequest req) throws ParseException, java.text.ParseException {
        JsonObject data = new JsonObject();

        // TODO - plus. outMember check
        String email = loginDto.getEmail();
        String snsType = loginDto.getSnsType();

        User findUser = userRepository.findByEmail(email);

        // 신규유저라면 회원가입 하고 바로 로그인
        if (findUser == null) {
            UserDto userDto = new UserDto();
            userDto.setEmail(loginDto.getEmail());
            userDto.setSnsType(loginDto.getSnsType());
            userService.signup(userDto);
        }
        //else if, userid 중복 , 그리고 snstype 다를때 user정보 update
        if (!findUser.getSnsName().equals(loginDto.getSnsType())) {
            System.out.println("AuthController.authorize");
            userService.updateUserInfo(findUser.getUserIdx(), loginDto.getSnsType());
        }

        User userOne = userRepository.findByEmail(email);

        //1년이상 장기 미이용 고객 return 커스텀
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        
        cal.add(Calendar.YEAR, -1);

        Date date1 = dateFormat.parse(dateFormat.format(cal.getTime()));
        Date date2 = userOne.getLastLoginDate();

        if (date2.before(date1)) {
            RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
            apiRes.setError(ErrCode.err_long_time_no_use_user.code());
            apiRes.setMessage(ErrCode.err_long_time_no_use_user.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
        
        //로그인
        TokenDto tokenDto = userService.login(email, snsType);
        data.addProperty("grantType", tokenDto.getGrantType());
        data.addProperty("accessToken",tokenDto.getAccessToken());
        data.addProperty("refreshToken", tokenDto.getRefreshToken());
        data.addProperty("user_seq", findUser.getUserIdx());


        // user 테이블에 Refreshtoken update
        userService.setRefreshToken(userOne.getUserIdx(), tokenDto.getRefreshToken());

        // user profile is empty check
        if (userOne.getProfileImageUrl() == null || ("").equals(userOne.getProfileImageUrl())) {
            data.addProperty("isProfileImageEmpty", "Y");
        } else {
            data.addProperty("isProfileImageEmpty", "N");
        }

        if (userOne.getNickName() == null || ("").equals(userOne.getNickName())) {
            data.addProperty("isNicknameEmpty", "Y");
        } else {
            data.addProperty("isNicknameEmpty", "N");
        }

        if (userOne.getCiValue() == null || ("").equals(userOne.getCiValue())) {
            data.addProperty("isCiValueEmpty", "Y");
        } else {
            data.addProperty("isCiValueEmpty", "N");
        }

        //user town's Information empty check
        List<Town> townList = townService.findAllList(userOne.getUserIdx());

        if (townList.size() != 0) {
            data.addProperty("isTownInfoEmpty", "N");
        } else {
            data.addProperty("isTownInfoEmpty", "Y");
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/refreshTokenValidation")
    public ResponseEntity<JsonObject> refreshTokenValidation(HttpServletRequest req, @RequestBody TokenDto token) throws ParseException {
        JsonObject data = new JsonObject();

        //유효한 토큰인지 확인
        //유효하다면 true. 만료됐거나 유효하지 않은 토큰이면 false.
        boolean tokenFlag = tokenProvider.validateToken(token.getRefreshToken());

        if (tokenFlag == false) {
            RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
            apiRes.setError(ErrCode.err_api_not_found_token.code());
            apiRes.setMessage(ErrCode.err_api_not_found_token.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
        String userEmail = UserMiningUtil.getUserInfo(token.getRefreshToken());
        User findUser = userRepository.findByEmail(userEmail);

        if (findUser != null) {
            if (findUser.getRefreshToken().equals(token.getRefreshToken())) {
                System.out.println("AuthController.refreshTokenValidation");
                TokenDto tokenDto = userService.login(findUser.getEmail(), findUser.getSnsName());
                data.addProperty("grantType", tokenDto.getGrantType());
                data.addProperty("accessToken",tokenDto.getAccessToken());
                data.addProperty("refreshToken", tokenDto.getRefreshToken());
                data.addProperty("user_seq", findUser.getUserIdx());

                userService.setRefreshToken(findUser.getUserIdx(), tokenDto.getRefreshToken());

                if (findUser.getProfileImageUrl() == null || ("").equals(findUser.getProfileImageUrl())) {
                    data.addProperty("isProfileImageEmpty", "Y");
                } else {
                    data.addProperty("isProfileImageEmpty", "N");
                }

                if (findUser.getNickName() == null || ("").equals(findUser.getNickName())) {
                    data.addProperty("isNicknameEmpty", "Y");
                } else {
                    data.addProperty("isNicknameEmpty", "N");
                }

                if (findUser.getCiValue() == null || ("").equals(findUser.getCiValue())) {
                    data.addProperty("isCiValueEmpty", "Y");
                } else {
                    data.addProperty("isCiValueEmpty", "N");
                }

                //user town's Information empty check
                List<Town> townList = townService.findAllN(findUser.getUserIdx());

                if (townList.size() != 0) {
                    data.addProperty("isTownInfoEmpty", "N");
                } else {
                    data.addProperty("isTownInfoEmpty", "Y");
                }
            } else {
                data.addProperty("message", "잘못된 토큰 정보");
            }
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/editUserCiValueAndName")
    public ResponseEntity<JsonObject> userCiValueUpdate(HttpServletRequest req, @RequestBody UserDto userDto) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        userService.updateCiValue(userDto.getCiValue(), userDto.getName(), findUser.getUserIdx());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

}
