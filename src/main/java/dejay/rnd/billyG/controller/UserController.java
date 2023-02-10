package dejay.rnd.billyG.controller;

import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TownService townService;


    public UserController(UserService userService, TokenProvider tokenProvider, UserRepository userRepository, TownService townService) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.townService = townService;
    }

    @PostMapping("/signup")
    public ResponseEntity<JsonObject> signup(@Valid @RequestBody UserDto userDto, HttpServletRequest req) {
        JsonObject data = new JsonObject();

        userService.signup(userDto);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    //TODO - fileService 추가 될때 이미지 업로드 구현
    @PostMapping("/editProfile")
    public ResponseEntity<JsonObject> editProfile( //@RequestPart (value = "image" , required = false) MultipartFile multipartFile,
                                                  @RequestParam (value = "profile_image_name", required = false) String profile_image_name,
                                                  @RequestParam (value = "nickname") String nickname,
                                                  HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        userService.setUserProfile(findUser.getUserIdx(), nickname, profile_image_name);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
    @PostMapping("/editMyTownInfo")
    public ResponseEntity<JsonObject> editMyTownInfo(@RequestParam (value = "lead_town_name") String lead_town_name,
                                                     @RequestParam (value = "town_name[]") String[] town_name, HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        townService.setUserTownInfo(findUser.getUserIdx(), lead_town_name, true);

        if (town_name.length > 0) {
            for (int i = 0; i < town_name.length; i++) {
                if(!town_name[i].isEmpty()) {
                    townService.setUserTownInfo(findUser.getUserIdx(), town_name[i], false);
                }
            }
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

}
