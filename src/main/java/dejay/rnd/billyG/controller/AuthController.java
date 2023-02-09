package dejay.rnd.billyG.controller;

import com.google.gson.*;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.LoginDto;
import dejay.rnd.billyG.dto.TokenDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
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

        List<User> userOne =userService.findById(email);

        System.out.println("userOne.size() = " + userOne.size());

        // 신규유저라면 회원가입 하고 바로 로그인
        if (userOne.size() != 1) {
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

        List<User> user =userService.findById(email);

        // user 테이블에 Refreshtoken update
        userService.setRefreshToken(user.get(0).getUserIdx(), tokenDto.getRefreshToken());

        // user profile is empty check
        if (user.get(0).getProfileImageUrl() == null) {
            data.addProperty("isProfileImageEmpty", "Y");
        } else {
            data.addProperty("isProfileImageEmpty", "N");
        }

        if (user.get(0).getNickName() == null) {
            data.addProperty("isNicknameEmpty", "Y");
        } else {
            data.addProperty("isNicknameEmpty", "N");
        }

        //user town's Information empty check
        List<Town> townList = townService.findAllN(user.get(0).getUserIdx());

        if (townList.size() != 0) {
            data.addProperty("isTownInfoEmpty", "N");
        } else {
            data.addProperty("isTownInfoEmpty", "Y");
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/refreshTokenValidation")
    public ResponseEntity<JsonObject> refreshTokenValidation(@RequestParam (value = "refreshToken") String refreshToken, HttpServletRequest req) throws ParseException {
        
        JsonObject data = new JsonObject();

        //유효한 토큰인지 확인
        //유효하다면 true. 만료됐거나 유효하지 않은 토큰이면 false.
        boolean tokenFlag = tokenProvider.validateToken(refreshToken);
        System.out.println("tokenFlag = " + tokenFlag);

        if (tokenFlag == false) {
            // TODO 잘못된 토큰인지, 만료된 토큰인지 구분할 수 있어야 함
            data.addProperty("isExpired", "Y");
            RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

        //tokenFlag true 일 때
        //refreshToken decode --> payload json parsing
        //userId 추출해서 회원정보 검색
        //refresh Token 검사해서 update.
        String[] chunks = refreshToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        JSONParser parser = new JSONParser();
        Object obj = parser.parse( payload );
        JSONObject jsonObj = (JSONObject) obj;

        String email = (String) jsonObj.get("sub");

        List<User> findUser = userService.findByEmail(email);

        System.out.println("findUser.size() = " + findUser.size());
        if (findUser.size() > 0) {
            if (findUser.get(0).getRefreshToken().equals(refreshToken)) {
                System.out.println("AuthController.refreshTokenValidation");
                TokenDto tokenDto = userService.login(findUser.get(0).getEmail(), findUser.get(0).getSnsName());
                data.addProperty("grantType", tokenDto.getGrantType());
                data.addProperty("accessToken",tokenDto.getAccessToken());
                data.addProperty("refreshToken", tokenDto.getRefreshToken());

                userService.setRefreshToken(findUser.get(0).getUserIdx(), tokenDto.getRefreshToken());
            } else {
                data.addProperty("InvalidRefreshToken", "잘못된 리프레시 토큰 정보");
            }
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }
}
