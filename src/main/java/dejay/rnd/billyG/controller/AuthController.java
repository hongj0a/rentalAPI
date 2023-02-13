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
     */
    @PostMapping("/authenticate")
    public ResponseEntity<JsonObject> authorize(@RequestBody LoginDto loginDto, HttpServletRequest req) {
        JsonObject data = new JsonObject();

        // plus. outMember check
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

        //로그인
        TokenDto tokenDto = userService.login(email, snsType);
        data.addProperty("grantType", tokenDto.getGrantType());
        data.addProperty("accessToken",tokenDto.getAccessToken());
        data.addProperty("refreshToken", tokenDto.getRefreshToken());

        User userOne = userRepository.findByEmail(email);

        // user 테이블에 Refreshtoken update
        userService.setRefreshToken(userOne.getUserIdx(), tokenDto.getRefreshToken());

        // user profile is empty check
        if (userOne.getProfileImageUrl() == null) {
            data.addProperty("isProfileImageEmpty", "Y");
        } else {
            data.addProperty("isProfileImageEmpty", "N");
        }

        if (userOne.getNickName() == null) {
            data.addProperty("isNicknameEmpty", "Y");
        } else {
            data.addProperty("isNicknameEmpty", "N");
        }

        //user town's Information empty check
        List<Town> townList = townService.findAllN(userOne.getUserIdx());

        if (townList.size() != 0) {
            data.addProperty("isTownInfoEmpty", "N");
        } else {
            data.addProperty("isTownInfoEmpty", "Y");
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/refreshTokenValidation")
    public ResponseEntity<JsonObject> refreshTokenValidation(@RequestBody TokenDto token, String refreshToken, HttpServletRequest req) throws ParseException {
        
        JsonObject data = new JsonObject();

        //유효한 토큰인지 확인
        //유효하다면 true. 만료됐거나 유효하지 않은 토큰이면 false.
        boolean tokenFlag = tokenProvider.validateToken(token.getRefreshToken());
        System.out.println("tokenFlag = " + tokenFlag);

        if (tokenFlag == false) {
            RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
            apiRes.setError(ErrCode.err_api_not_found_token.code());
            apiRes.setMessage(ErrCode.err_api_not_found_token.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
        String userEmail = UserMiningUtil.getUserInfo(refreshToken);
        User findUser = userRepository.findByEmail(userEmail);

        if (findUser != null) {
            if (findUser.getRefreshToken().equals(refreshToken)) {
                System.out.println("AuthController.refreshTokenValidation");
                TokenDto tokenDto = userService.login(findUser.getEmail(), findUser.getSnsName());
                data.addProperty("grantType", tokenDto.getGrantType());
                data.addProperty("accessToken",tokenDto.getAccessToken());
                data.addProperty("refreshToken", tokenDto.getRefreshToken());

                userService.setRefreshToken(findUser.getUserIdx(), tokenDto.getRefreshToken());
            } else {
                data.addProperty("message", "잘못된 토큰 정보");
            }
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }
}
