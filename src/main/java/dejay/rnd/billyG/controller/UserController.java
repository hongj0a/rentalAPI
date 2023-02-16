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

import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TownService townService;
    private final FileUploadService uploadService;


    public UserController(UserService userService, TokenProvider tokenProvider, UserRepository userRepository, TownService townService, FileUploadService uploadService) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.townService = townService;
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
            System.out.println("UserController.editProfile");
            //imageUrl = multipartFile.getOriginalFilename();
            uploadService.upload(multipartFile);
            imageUrl = multipartFile.getOriginalFilename();
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

        townService.setUserTownInfo(findUser.getUserIdx(), userTown.getLeadTownName(), true);

        if (userTown.getTowns().length > 0) {
            for (int i = 0; i < userTown.getTowns().length; i++) {
                if(!userTown.getTowns()[i].isEmpty()) {
                    townService.setUserTownInfo(findUser.getUserIdx(), userTown.getTowns()[i], false);
                }
            }
        }

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
        JsonArray townNames = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        data.addProperty("profileImage", findUser.getProfileImageUrl());
        data.addProperty("nickName", findUser.getNickName());
        data.addProperty("phoneNumber", findUser.getPhoneNum());

        List<Town> towns = townService.findAllN(findUser.getUserIdx());
        towns.forEach(
                town -> {
                    JsonObject townInfo = new JsonObject();
                    if (town.isLeadTown()) {
                        townInfo.addProperty("leadTown", town.getTownName());
                    } else {
                        townInfo.addProperty("townName", town.getTownName());
                    }
                    townNames.add(townInfo);
                }
        );
        data.add("townList", townNames);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

}
