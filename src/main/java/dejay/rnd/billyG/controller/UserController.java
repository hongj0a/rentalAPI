package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repository.TownRepository;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TownService townService;
    private final TownRepository townRepository;
    private final FileUploadService uploadService;


    public UserController(UserService userService, TokenProvider tokenProvider, UserRepository userRepository, TownService townService, TownRepository townRepository, FileUploadService uploadService) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.townService = townService;
        this.townRepository = townRepository;
        this.uploadService = uploadService;
    }

    @PostMapping("/signup")
    public ResponseEntity<JsonObject> signup(@Valid @RequestBody UserDto userDto, HttpServletRequest req) {
        JsonObject data = new JsonObject();

        userService.signup(userDto);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/editProfile")
    public ResponseEntity<JsonObject> editProfile(@RequestPart(value="image", required = false) MultipartFile multipartFile,
            @RequestParam(value="nickName", required = false) String nickName,
                                                  HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        String imageUrl = "";
        String userNickName = "";

        if (multipartFile != null) {
            ImageFile file = uploadService.upload(multipartFile);
            imageUrl = file.getFileName();
        }

        if (nickName != null) {
            userNickName = nickName;
        }
        userService.setUserProfile(findUser.getUserIdx(), userNickName, imageUrl);


        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());


    }
    @PostMapping("/editMyTownInfo")
    public ResponseEntity<JsonObject> editMyTownInfo(@RequestBody UserDto userTown,
                                                     HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);


        Map<Integer, Long> userTowns = new HashMap<>();
        Town userTownIdx = townService.setTowns(userTown.getLeadTownName());
        userTowns.put(0, userTownIdx.getTownIdx());

        if (userTown.getTowns().length > 0) {
            for (int i = 0; i < userTown.getTowns().length; i++) {
                if(!userTown.getTowns()[i].isEmpty()) {
                    //townService.setUserTownInfo(findUser.getUserIdx(), userTown.getTowns()[i], false);
                    //town테이블에 있는 타운인지 조회해서 인덱스 리턴받고 배열에 저장
                    userTownIdx = townService.setTowns(userTown.getTowns()[i]);
                    userTowns.put(i+1, userTownIdx.getTownIdx());
                }
            }
        }
        System.out.println("userTowns = " + userTowns);


        userService.updateUserTownInfo(userTowns, findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/doubleCheck")
    public ResponseEntity<JsonObject> doubleCheck(@RequestParam (value = "nickName") String nickName,
                                                      HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();

        List<User> findUser = userService.findByNickName(nickName);

        if (findUser.size() > 0) {
            RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
            apiRes.setError(ErrCode.err_api_duplicate_nickname.code());
            apiRes.setMessage(ErrCode.err_api_duplicate_nickname.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getAdditionalInfo")
    public ResponseEntity<JsonObject> getAdditionalInfo(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        data.addProperty("profileImage", findUser.getProfileImageUrl());
        data.addProperty("nickName", findUser.getNickName());
        data.addProperty("phoneNumber", findUser.getPhoneNum());

        //List<Town> towns = townService.findAllN(findUser.getUserIdx());
        /*Town lead = townRepository.findByLeadTownAndUser_userIdx(true,findUser.getUserIdx());
        data.addProperty("leadTown", lead.getTownName());*/

        data.addProperty("leadTown", findUser.getLeadTown());

        //List<Town> personal_towns = townService.findTownInfo(findUser);
        townArr.add(findUser.getTown1());
        townArr.add(findUser.getTown2());
        townArr.add(findUser.getTown3());
        townArr.add(findUser.getTown4());


        data.add("townList", townArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

}
