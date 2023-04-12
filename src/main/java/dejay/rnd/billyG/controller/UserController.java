package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.AlarmDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;
    private final TownService townService;
    private final TownRepository townRepository;
    private final FileUploadService uploadService;
    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final GradeRepository gradeRepository;
    private final BlockPostRepository blockPostRepository;

    public UserController(UserService userService, UserRepository userRepository, TownService townService, TownRepository townRepository, FileUploadService uploadService, RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository, GradeRepository gradeRepository, BlockPostRepository blockPostRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.townService = townService;
        this.townRepository = townRepository;
        this.uploadService = uploadService;
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.gradeRepository = gradeRepository;
        this.blockPostRepository = blockPostRepository;
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

        userService.updateUserTownInfo(userTowns, findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/editPhoneNumber")
    public ResponseEntity<JsonObject> userCiValueUpdate(HttpServletRequest req, @RequestBody UserDto userDto) throws ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        if (findUser.getCiValue().equals(userDto.getCiValue())) {
            userService.updateCiValue(userDto.getPhoneNumber(), findUser);

            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        } else {
            apiRes.setError(ErrCode.err_api_is_inconsistency.code());
            apiRes.setMessage(ErrCode.err_api_is_inconsistency.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }
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
        data.addProperty("email", findUser.getEmail());

        //필수니깐 없을수가 없음
        Town findLeadTown = townRepository.getOne(findUser.getLeadTown());
        data.addProperty("leadTown", findLeadTown.getTownName());

        if (findUser.getTown1() != null) {
            Town findTown1 = townRepository.getOne(findUser.getTown1());
            townArr.add(findTown1.getTownName());
        }

        if (findUser.getTown2() != null) {
            Town findTown2 = townRepository.getOne(findUser.getTown2());
            townArr.add(findTown2.getTownName());
        }

        if (findUser.getTown3() != null) {
            Town findTown3 = townRepository.getOne(findUser.getTown3());
            townArr.add(findTown3.getTownName());
        }

        if (findUser.getTown4() != null) {
            Town findTown4 = townRepository.getOne(findUser.getTown4());
            townArr.add(findTown4.getTownName());
        }

        data.add("townList", townArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getUserDetailPage")
    public ResponseEntity<JsonObject> getUserDetailPage(@RequestParam (value = "userIdx") Long userIdx,
                                                             HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();

        User otherUser = userRepository.getOne(userIdx);
        Grade grade = gradeRepository.findTop1ByOrderByGradeScoreDesc();

        //필수니깐 없을수가 없음
        Town findLeadTown = townRepository.getOne(otherUser.getLeadTown());
        data.addProperty("leadTown", findLeadTown.getTownName());

        if (otherUser.getTown1() != null) {
            Town findTown1 = townRepository.getOne(otherUser.getTown1());
            townArr.add(findTown1.getTownName());
        }

        if (otherUser.getTown2() != null) {
            Town findTown2 = townRepository.getOne(otherUser.getTown2());
            townArr.add(findTown2.getTownName());
        }

        if (otherUser.getTown3() != null) {
            Town findTown3 = townRepository.getOne(otherUser.getTown3());
            townArr.add(findTown3.getTownName());
        }

        if (otherUser.getTown4() != null) {
            Town findTown4 = townRepository.getOne(otherUser.getTown4());
            townArr.add(findTown4.getTownName());
        }

        data.addProperty("profileImage", otherUser.getProfileImageUrl());
        data.addProperty("nickName", otherUser.getNickName());
        data.addProperty("grade", otherUser.getUserLevel());
        data.addProperty("email", otherUser.getEmail());
        data.addProperty("idEmail", otherUser.getIdEmail());
        data.addProperty("activityScore", otherUser.getActivityScore());
        data.addProperty("maxScore", grade.getGradeScore());
        data.addProperty("leadTown", otherUser.getLeadTown());
        data.addProperty("starPoint", otherUser.getStarPoint());
        data.add("townList", townArr);


        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
    @GetMapping("/getOtherUserDetailPageList")
    public ResponseEntity<JsonObject> getOtherUserDetailPageList(@RequestParam (value = "userIdx") Long userIdx,
                                                            @RequestParam (value = "type") int type,
                                                            Pageable pageable,
                                                            HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray renArr = new JsonArray();
        JsonArray reviewArr = new JsonArray();

        User otherUser = userRepository.getOne(userIdx);
        if (type == 1) {
            //렌탈중인 게시글
            Page<Rental> etcRentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotIn(otherUser.getUserIdx(), true, false, new int[]{4}, pageable);
            List<Rental> etc = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotIn(otherUser.getUserIdx(), true, false, new int[]{4});
            etcRentals.forEach(
                    etcs -> {
                        JsonObject etcRental = new JsonObject();

                        List<RentalImage> renImgs = rentalImageRepository.findByRental_rentalIdx(etcs.getRentalIdx());

                        etcRental.addProperty("rentalSeq", etcs.getRentalIdx());
                        if (renImgs.size() != 0) {
                            etcRental.addProperty("imageUrl", renImgs.get(0).getImageUrl());
                        }

                        etcRental.addProperty("title", etcs.getTitle());
                        etcRental.addProperty("dailyRentalFee", etcs.getRentalPrice());

                        renArr.add(etcRental);
                    }
            );
            data.add("rentals", renArr);
            data.addProperty("totalCount", etc.size());
        } else if (type == 2){
            //후기 전체 list
            Page<Review> findReviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAt(userIdx, true, false, pageable);
            List<Review> reviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAt(userIdx, true, false);

            findReviews.forEach(
                    review -> {
                        JsonObject rvs = new JsonObject();

                        rvs.addProperty("renterIdx", review.getTransaction().getUser().getUserIdx());
                        rvs.addProperty("renterImage", review.getTransaction().getUser().getProfileImageUrl());
                        rvs.addProperty("renterNickName", review.getTransaction().getUser().getNickName());
                        rvs.addProperty("renterLeadTown", review.getTransaction().getUser().getLeadTown());
                        rvs.addProperty("reviewRegDate", review.getCreateAt().getTime());
                        rvs.addProperty("reviewIdx", review.getReviewIdx());
                        rvs.addProperty("reviewContent", review.getReviewContent());
                        rvs.addProperty("reviewStarPoint", review.getReviewScore());

                        reviewArr.add(rvs);
                    }
            );
            data.add("reviewList", reviewArr);
            data.addProperty("reviewsTotalCount", reviews.size());

        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getReview")
    public ResponseEntity<JsonObject> getReview(@RequestParam (value = "reviewIdx") Long reviewIdx,
                                                 HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray rvImg = new JsonArray();

        //삭제검사 생략, 애초에 리뷰 리스트 가져올 때 delete_yn 필터링 됨.

        Review review = reviewRepository.getOne(reviewIdx);

        data.addProperty("userIdx", review.getTransaction().getUser().getUserIdx());
        data.addProperty("nickName", review.getTransaction().getUser().getNickName());
        data.addProperty("starPoint", review.getReviewScore());
        data.addProperty("title", review.getTransaction().getRental().getTitle());
        data.addProperty("content", review.getReviewContent());

        List<ReviewImage> images = reviewImageRepository.findByReview_ReviewIdx(review.getReviewIdx());

        if (images.size() != 0) {
            images.forEach(
                    ri -> {
                        JsonObject rvs = new JsonObject();
                        rvs.addProperty("imageSeq", ri.getImageIdx());
                        rvs.addProperty("imageUrl", ri.getImageUrl());

                        rvImg.add(rvs);
                    }
            );
            data.add("reviewImageList", rvImg);
        } else {

            data.add("reviewImageList", rvImg);
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/emailCheck")
    public ResponseEntity<JsonObject> emailCheck(@RequestParam (value = "email") String email,
                                                  HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();

        User findUser = userRepository.findByEmail(email);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        if (findUser != null) {
            apiRes.setError(ErrCode.err_api_duplicate_email.code());
            apiRes.setMessage(ErrCode.err_api_duplicate_email.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        } else {
            apiRes.setError(ErrCode.err_api_available_email.code());
            apiRes.setMessage(ErrCode.err_api_available_email.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

    }
    @PostMapping("/setEmail")
    public ResponseEntity<JsonObject> setEmail(@RequestParam(value="email", required = false) String email,
                                                  HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        findUser.setIdEmail(email);
        userService.updateUser(findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());


    }

    @PostMapping("/setAlarm")
    public ResponseEntity<JsonObject> setAlarm(@RequestBody AlarmDto alarmDto,
                                               HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        //컬럼을 파야될듯
        findUser.setDoNotDisturbTimeYn(alarmDto.isDoNotDisturbTimeYn());
        if (alarmDto.isDoNotDisturbTimeYn() == true) {
            System.out.println("UserController.setEmail######");
        }
        findUser.setChatNoticeYn(alarmDto.isChatNoticeYn());
        findUser.setActivityNoticeYn(alarmDto.isActivityNoticeYn());
        findUser.setMarketingNoticeYn(alarmDto.isMarketingNoticeYn());
        userService.updateUser(findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());


    }
}
