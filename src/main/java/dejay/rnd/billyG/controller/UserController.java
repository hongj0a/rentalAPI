package dejay.rnd.billyG.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.config.ImageProperties;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.dto.AlarmDto;
import dejay.rnd.billyG.dto.OutDto;
import dejay.rnd.billyG.dto.ReviewDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repositoryImpl.UserCountRepositories;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.ReviewService;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
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

    private final TermsRepository termsRepository;
    private final CategoryRepository categoryRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final TransactionRepository transactionRepository;
    private final LikeRepository likeRepository;
    private final UserCountRepository userCountRepository;
    private final UserCountRepositories userCountRepositories;
    private final ReviewService reviewService;
    private final Path fileStorageLocation;
    private final UserEvaluationRepository userEvaluationRepository;
    private final ArbitrationRepository arbitrationRepository;

    public UserController(ImageProperties imageProperties, UserService userService, UserRepository userRepository, TownService townService, TownRepository townRepository, FileUploadService uploadService, RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository, GradeRepository gradeRepository, TermsRepository termsRepository, CategoryRepository categoryRepository, StatusHistoryRepository statusHistoryRepository, TransactionRepository transactionRepository, LikeRepository likeRepository, UserCountRepository userCountRepository, UserCountRepositories userCountRepositories, ReviewService reviewService, UserEvaluationRepository userEvaluationRepository, ArbitrationRepository arbitrationRepository) {
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
        this.termsRepository = termsRepository;
        this.categoryRepository = categoryRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.transactionRepository = transactionRepository;
        this.likeRepository = likeRepository;
        this.userCountRepository = userCountRepository;
        this.userCountRepositories = userCountRepositories;
        this.reviewService = reviewService;
        this.fileStorageLocation = Paths.get(imageProperties.getDefaultPath())
                .toAbsolutePath().normalize();
        this.userEvaluationRepository = userEvaluationRepository;
        this.arbitrationRepository = arbitrationRepository;
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

        if (multipartFile != null && nickName != null) {
            userNickName = nickName;
            userService.setUserProfile(findUser.getUserIdx(), userNickName, imageUrl);
        } else if (nickName == null && multipartFile != null) {
            userService.setUserProfileImageUrl(findUser.getUserIdx(), imageUrl);
        } else if (nickName != null && multipartFile == null){
            userNickName = nickName;
            userService.setUserNickName(findUser.getUserIdx(), userNickName);
        }



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
        data.addProperty("name", findUser.getName());
        data.addProperty("phoneNumber", findUser.getPhoneNum());
        data.addProperty("email", findUser.getEmail());
        data.addProperty("signPath", findUser.getSnsName());
        data.addProperty("disturbInfo", findUser.isDoNotDisturbTimeYn());

        if (findUser.getDoNotDisturbStartHour() != null && findUser.getDoNotDisturbStartMinute() != null) {
            data.addProperty("startHour", findUser.getDoNotDisturbStartHour());
            data.addProperty("startMinute", findUser.getDoNotDisturbStartMinute());
        } else {
            data.addProperty("startHour","");
            data.addProperty("startMinute" ,"");
        }

        if (findUser.getDoNotDisturbEndHour() != null && findUser.getDoNotDisturbEndMinute() != null) {
            data.addProperty("endHour", findUser.getDoNotDisturbEndHour());
            data.addProperty("endMinute", findUser.getDoNotDisturbEndMinute());
        } else {
            data.addProperty("endHour","");
            data.addProperty("endMinute" ,"");
        }

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
                                                             HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();

        User otherUser;

        if (userIdx != 0) {
            otherUser = userRepository.getOne(userIdx);
        } else {
            String acToken = req.getHeader("Authorization").substring(7);
            String userEmail = UserMiningUtil.getUserInfo(acToken);
            otherUser = userRepository.findByEmail(userEmail);
        }

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
        data.addProperty("name", otherUser.getName());
        data.addProperty("phoneNumber", otherUser.getPhoneNum());
        data.addProperty("snsType", otherUser.getSnsName());
        data.addProperty("grade", otherUser.getUserLevel());
        data.addProperty("email", otherUser.getEmail());
        data.addProperty("receptionEmail", otherUser.getIdEmail());
        data.addProperty("activityScore", otherUser.getActivityScore());
        data.addProperty("maxScore", grade.getGradeScore());
        data.addProperty("starPoint", otherUser.getStarPoint());
        data.addProperty("disturbTimeYn", otherUser.isDoNotDisturbTimeYn());

        if (otherUser.isDoNotDisturbTimeYn() == true) {
            data.addProperty("disturbStartHour", otherUser.getDoNotDisturbStartHour());
            data.addProperty("disturbStartMinute", otherUser.getDoNotDisturbStartMinute());
            data.addProperty("disturbEndHour", otherUser.getDoNotDisturbEndHour());
            data.addProperty("disturbEndMinute", otherUser.getDoNotDisturbEndMinute());
        } else if (otherUser.isDoNotDisturbTimeYn() == false && (otherUser.getDoNotDisturbStartHour() != null ||
                otherUser.getDoNotDisturbStartMinute() != null || otherUser.getDoNotDisturbEndHour() != null ||
                otherUser.getDoNotDisturbEndMinute() != null)) {
            data.addProperty("disturbStartHour", otherUser.getDoNotDisturbStartHour());
            data.addProperty("disturbStartMinute", otherUser.getDoNotDisturbStartMinute());
            data.addProperty("disturbEndHour", otherUser.getDoNotDisturbEndHour());
            data.addProperty("disturbEndMinute", otherUser.getDoNotDisturbEndMinute());
        }
        data.add("townList", townArr);


        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
    @GetMapping("/getOtherUserDetailPageList")
    public ResponseEntity<JsonObject> getOtherUserDetailPageList(@RequestParam (value = "userIdx") Long userIdx,
                                                            @RequestParam (value = "type") int type,
                                                            Pageable pageable,
                                                            HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray renArr = new JsonArray();
        JsonArray reviewArr = new JsonArray();

        User otherUser;

        if (userIdx != 0) {
            otherUser = userRepository.getOne(userIdx);
        } else {
            String acToken = req.getHeader("Authorization").substring(7);
            String userEmail = UserMiningUtil.getUserInfo(acToken);
            otherUser = userRepository.findByEmail(userEmail);
        }

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
            //받은후기
            Page<Review> findReviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(otherUser.getUserIdx(), true, false, pageable);
            List<Review> reviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(otherUser.getUserIdx(), true, false);

            findReviews.forEach(
                    review -> {
                        JsonObject rvs = new JsonObject();

                        rvs.addProperty("renterIdx", review.getTransaction().getUser().getUserIdx());
                        rvs.addProperty("renterImage", review.getTransaction().getUser().getProfileImageUrl());
                        if (review.getTransaction().getUser().getUserIdx() == otherUser.getUserIdx()) {
                            rvs.addProperty("isMine", true);
                        } else {
                            rvs.addProperty("isMine", false);
                        }
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

        } else if (type == 3) {
            //보낸후기
            Page<Review> findReviews = reviewRepository.findByRenterIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(otherUser.getUserIdx(), true, false, pageable);
            List<Review> reviews = reviewRepository.findByRenterIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(otherUser.getUserIdx(), true, false);

            findReviews.forEach(
                    review -> {
                        JsonObject rvs = new JsonObject();

                        rvs.addProperty("renterIdx", review.getTransaction().getUser().getUserIdx());
                        rvs.addProperty("renterImage", review.getTransaction().getUser().getProfileImageUrl());
                        if (review.getTransaction().getUser().getUserIdx() == otherUser.getUserIdx()) {
                            rvs.addProperty("isMine", true);
                        } else {
                            rvs.addProperty("isMine", false);
                        }
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
                                                 HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray rvImg = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        //삭제검사 생략, 애초에 리뷰 리스트 가져올 때 delete_yn 필터링 됨.

        Review review = reviewRepository.getOne(reviewIdx);

        if (findUser.getUserIdx() == review.getRenterIdx()) {
            data.addProperty("isMine", true);
        } else {
            data.addProperty("isMine", false);
        }

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

        User findUser = userRepository.findByEmailOrIdEmail(email, email);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        if (findUser != null) {
            apiRes.setError(ErrCode.err_api_duplicate_email.code());
            apiRes.setMessage(ErrCode.err_api_duplicate_email.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        } else {
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

    }

    @GetMapping("/getPushSettingInfo")
    public ResponseEntity<JsonObject> getPushSettingInfo(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        data.addProperty("chatNoticeYn", findUser.isChatNoticeYn());
        data.addProperty("marketingNoticeType", findUser.getMarketingNoticeType());
        data.addProperty("activityNoticeYn", findUser.isActivityNoticeYn());
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

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

        findUser.setDoNotDisturbTimeYn(alarmDto.isDoNotDisturbTimeYn());
        if (alarmDto.isDoNotDisturbTimeYn() == true) {
            findUser.setDoNotDisturbStartHour(alarmDto.getStartHour());
            findUser.setDoNotDisturbStartMinute(alarmDto.getStartMinute());
            findUser.setDoNotDisturbEndHour(alarmDto.getEndHour());
            findUser.setDoNotDisturbEndMinute(alarmDto.getEndMinute());
        }
        findUser.setChatNoticeYn(alarmDto.isChatNoticeYn());
        findUser.setActivityNoticeYn(alarmDto.isActivityNoticeYn());
        findUser.setMarketingNoticeType(alarmDto.getMarketingNoticeType());

        findUser.setUpdator(findUser.getEmail());
        userService.updateUser(findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getUseTerms")
    public ResponseEntity<JsonObject> getUseTerms(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        Terms getTerm = termsRepository.findTopByReservationDateLessThanEqualOrderByReservationDateDesc(now_date);

        data.addProperty("title", getTerm.getTitle());
        data.addProperty("content", getTerm.getContent());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/setWithDraw")
    public ResponseEntity<JsonObject> setWithDraw(@RequestBody OutDto outDto,
                                                  HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        Category findCategory = categoryRepository.getOne(outDto.getTypeIdx());

        findUser.setCategory(findCategory);
        findUser.setStatus(30);
        findUser.setDeleteAt(now_date);
        if (outDto.getBlockContent() != null) {
            findUser.setOutReason(outDto.getBlockContent());
        }
        findUser.setUpdator(findUser.getEmail());
        userService.updateUser(findUser);

        StatusHistory sh = new StatusHistory();
        sh.setUser(findUser);
        sh.setDeleteAt(now_date);
        sh.setCreateAt(now_date);
        sh.setStatus(30);

        statusHistoryRepository.save(sh);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getTransactionStep")
    public ResponseEntity<JsonObject> getTransactionStep(HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray statusArr = new JsonArray();

        LinkedHashMap<Integer, String> statusMap = new LinkedHashMap<>();
        statusMap.put(10, "매칭대기");
        statusMap.put(20, "매칭완료");
        statusMap.put(30, "렌탈중");
        statusMap.put(40, "물품인수");
        statusMap.put(50, "물품반납");
        statusMap.put(60, "이의신청");
        statusMap.put(70, "렌탈완료");

        for (Map.Entry<Integer, String> pair : statusMap.entrySet()) {
            JsonObject status = new JsonObject();

            status.addProperty("stepKey", pair.getKey());
            status.addProperty("step", pair.getValue());

            statusArr.add(status);
        }


        data.add("stepList", statusArr);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getSendOrReceivedRentals")
    public ResponseEntity<JsonObject> getSendOrReceivedRentals(@RequestParam(value="rentalFlag") int rentalFlag,
                                                               @RequestParam(value="rentalStatus") int rentalStatus[], Pageable pageable,
                                                               HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray renArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Page<Transaction> transactions;
        List<Transaction> tranSize;
        AtomicReference<String> str = new AtomicReference<>("");


        if (rentalFlag == 0) {
        //rental received
            if (rentalStatus.length == 0) {
                //전체는 0으로
                transactions = transactionRepository.findByUser_userIdxAndCancelYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);
                tranSize = transactionRepository.findByUser_userIdxAndCancelYn(findUser.getUserIdx(), false);
            } else {
                //status값 별로 조회
                transactions = transactionRepository.findByUser_userIdxAndCancelYnAndRenterStatusInOrderByCreateAtDesc(findUser.getUserIdx(), false, rentalStatus, pageable);
                tranSize = transactionRepository.findByUser_userIdxAndCancelYnAndRenterStatusIn(findUser.getUserIdx(), false, rentalStatus);
            }
        } else {
            //rental send
            if (rentalStatus.length == 0) {
                //여기도 마찬가지로 전체 0
                transactions = transactionRepository.findByRental_User_userIdxAndCancelYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);
                tranSize = transactionRepository.findByRental_User_userIdxAndCancelYn(findUser.getUserIdx(), false);
            } else {
                //status값 별로 조회
                transactions = transactionRepository.findByRental_User_userIdxAndCancelYnAndOwnerStatusInOrderByCreateAtDesc(findUser.getUserIdx(), false, rentalStatus, pageable);
                tranSize = transactionRepository.findByRental_User_userIdxAndCancelYnAndOwnerStatusIn(findUser.getUserIdx(), false, rentalStatus);

            }
        }

        transactions.forEach(
                tr -> {
                    JsonObject trs = new JsonObject();
                    trs.addProperty("historySeq", tr.getTransactionIdx());
                    trs.addProperty("rentalSeq", tr.getRental().getRentalIdx());
                    List<RentalImage> img = rentalImageRepository.findByRental_rentalIdx(tr.getRental().getRentalIdx());
                    if (img.size() != 0) {
                        trs.addProperty("imageSeq", img.get(0).getImageIdx());
                        trs.addProperty("imageUrl", img.get(0).getImageUrl());
                    }
                    trs.addProperty("title", tr.getRental().getTitle());
                    if (tr.getOwnerStatus() >= tr.getRenterStatus()) {
                        /**
                         * 10 : 매칭대기
                         * 30 : 렌탈중
                         * 60 : 이의신청
                         * 70 : 렌탈완료
                         */
                        switch (tr.getOwnerStatus()) {
                            case 10 :
                                str.set("매칭대기");
                                break;
                            case 30 :
                                str.set("렌탈중");
                                break;
                            case 60 :
                                str.set("이의신청");
                                break;
                            default :
                                str.set("렌탈완료");
                                break;
                        }
                        trs.addProperty("status", str.get());
                    } else {
                        /**
                         * 10 : 매칭대기
                         * 20 : 매칭완료
                         * 40 : 물품인수
                         * 50 : 물품반납
                         * 70 : 렌탈완료
                         */
                        switch (tr.getOwnerStatus()) {
                            case 10 :
                                str.set("매칭대기");
                                break;
                            case 20 :
                                str.set("매칭완료");
                                break;
                            case 40 :
                                str.set("물품인수");
                                break;
                            case 50 :
                                str.set("물품반납");
                                break;
                            default :
                                str.set("렌탈완료");
                                break;
                        }
                        trs.addProperty("status", str.get());
                    }
                    trs.addProperty("dailyRentalFee", tr.getRental().getRentalPrice());

                    renArr.add(trs);
                }
        );

        data.add("transactions", renArr);
        data.addProperty("totalCount", tranSize.size());

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getLikeList")
    public ResponseEntity<JsonObject> getLikeList(Pageable pageable,
                                                  HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray myArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);


        Gson gson = new Gson();
        Page<Likes> likes = likeRepository.findByUser_userIdxAndDeleteYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);
        List<Likes> likeSize = likeRepository.findByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);

        likes.forEach(
                li -> {
                    JsonObject my = new JsonObject();
                    boolean rentalStatus;

                    my.addProperty("rentalSeq", li.getRental().getRentalIdx());
                    my.addProperty("title", li.getRental().getTitle());

                    List<RentalImage> images = rentalImageRepository.findByRental_rentalIdx(li.getRental().getRentalIdx());
                    if (images.size() != 0) {
                        my.addProperty("imageSeq", images.get(0).getImageIdx());
                        my.addProperty("imageUrl", images.get(0).getImageUrl());
                    }

                    if (li.getRental().getStatus() == 1) {
                        rentalStatus = true;
                    } else {
                        rentalStatus = false;
                    }

                    my.addProperty("dailyRentalFee", li.getRental().getRentalPrice());
                    my.addProperty("regDate", li.getRental().getCreateAt().getTime());
                    my.addProperty("status", rentalStatus);

                    //town 리스트 추출
                    List<String> tLst = new ArrayList<>();
                    if (null != li.getRental().getLeadTown()) {
                        Town Ltown = townRepository.getOne(li.getRental().getLeadTown());
                        tLst.add(Ltown.getTownName());
                    }
                    if (null != li.getRental().getTown1()) {
                        Town town1 = townRepository.getOne(li.getRental().getTown1());
                        tLst.add(town1.getTownName());
                    }
                    if (null != li.getRental().getTown2()) {
                        Town town2 = townRepository.getOne(li.getRental().getTown2());
                        tLst.add(town2.getTownName());
                    }
                    if (null != li.getRental().getTown3()) {
                        Town town3 = townRepository.getOne(li.getRental().getTown3());
                        tLst.add(town3.getTownName());
                    }
                    if (null != li.getRental().getTown4()) {
                        Town town4 = townRepository.getOne(li.getRental().getTown4());
                        tLst.add(town4.getTownName());
                    }
                    my.add("towns", gson.toJsonTree(tLst));

                    myArr.add(my);
                }
        );

        data.add("myLikes", myArr);
        data.addProperty("totalCount", likeSize.size());
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    //리뷰 등록전에 뿌려지는 페이지
    //렌탈게시글 정보, 평가항목 목록
    @GetMapping("/getReviewInfo")
    public ResponseEntity<JsonObject> getReviewInfo(@RequestParam(value="rentalIdx") Long rentalIdx,
                                                    @RequestParam(value="historyIdx") Long historyIdx,
                                                    HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray itemArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(rentalIdx);

        data.addProperty("rentalSeq", findRental.getRentalIdx());
        data.addProperty("historySeq", historyIdx);
        List<RentalImage> images = rentalImageRepository.findByRental_rentalIdx(findRental.getRentalIdx());

        if (images.size() != 0) {
            data.addProperty("imageSeq", images.get(0).getImageIdx());
            data.addProperty("imageUrl", images.get(0).getImageUrl());
        }
        data.addProperty("title", findRental.getTitle());
        data.addProperty("regDate", findRental.getCreateAt().getTime());
        data.addProperty("dailyRentalFee", findRental.getRentalPrice());

        if(findRental.getLeadTown() != null) {
            Town getTown = townRepository.getOne(findRental.getLeadTown());
            data.addProperty("leadTown", getTown.getTownName());
        }

        List<Category> categories = categoryRepository.findAllByCategoryTypeAndOrderNumNotInOrderByOrderNum("4", new int[]{9999});

        categories.forEach(
                category -> {
                    JsonObject ct = new JsonObject();
                    ct.addProperty("typeIdx", category.getCategoryIdx());
                    ct.addProperty("typeName", category.getName());

                    itemArr.add(ct);
                }
        );

        data.add("categoryInfo", itemArr);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/setReview", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> setReview(@RequestParam (value = "images", required = false) List<MultipartFile> images,
                                                @RequestParam (value = "content", required = false) String content,
                                                @RequestParam (value = "score") String score[],
                                                @RequestParam (value ="historyIdx") String historyIdx,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        int total = 0;
        float avg;
        String starPoint;

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());
        Transaction transaction = transactionRepository.getOne(Long.valueOf(historyIdx));
        UserCount receiveCnt = userCountRepository.findByUser_UserIdx(transaction.getRental().getUser().getUserIdx());

        Review review = new Review();

        review.setReviewScore(Integer.valueOf(score.length));

        if (content != null) {
            review.setReviewContent(content);
        }
        review.setRenterIdx(transaction.getUser().getUserIdx());
        review.setOwnerIdx(transaction.getRental().getUser().getUserIdx());
        review.setTransaction(transaction);

        Review getReview = reviewRepository.save(review);

        for (int i = 0; i < score.length; i++) {
            UserEvaluation userEvaluation = new UserEvaluation();

            userEvaluation.setUser(transaction.getRental().getUser());
            Category category = categoryRepository.getOne(Long.valueOf(score[i]));
            userEvaluation.setCategory(category);

            userEvaluationRepository.save(userEvaluation);
        }


        if (userCount != null) {
            userCount.setGiveReviewCnt(userCount.getGiveReviewCnt()+1);

        } else {
            UserCount newCount = new UserCount();
            newCount.setUser(findUser);
            newCount.setGiveReviewCnt(1L);

            userCountRepositories.save(newCount);
        }

        if (receiveCnt != null) {
            receiveCnt.setReceiveReviewCnt(receiveCnt.getReceiveReviewCnt()+1);
        } else {
            UserCount newCount = new UserCount();
            newCount.setUser(transaction.getRental().getUser());
            newCount.setReceiveReviewCnt(1L);

            userCountRepositories.save(newCount);
        }
        if (images.size() != 0) {
            for (int i = 0; i < images.size(); i++) {
                ReviewImage reviewImage = new ReviewImage();
                ImageFile file = uploadService.upload(images.get(i));

                reviewImage.setReview(getReview);
                reviewImage.setImageUrl(file.getFileName());

                reviewImageRepository.save(reviewImage);
            }
        }

        //리뷰 별점 계산
        List<Review> reviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(transaction.getRental().getUser().getUserIdx(), true, false);
        for (int i = 0; i < reviews.size(); i++) {
            total += reviews.get(i).getReviewScore();
        }
        avg = total / reviews.size();
        starPoint = String.format("%.1f", avg);

        User starUser = userRepository.getOne(transaction.getRental().getUser().getUserIdx());
        starUser.setStarPoint(starPoint);
        userService.updateUser(starUser);

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/deleteReview")
    public ResponseEntity<JsonObject> deleteReview(HttpServletRequest req,
                                                   @RequestBody ReviewDto reviewDto) throws ParseException {
        JsonObject data = new JsonObject();
        int total = 0;
        float avg;
        String starPoint;
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        
        Review findReview = reviewRepository.getOne(reviewDto.getReviewIdx());
        Transaction transaction = transactionRepository.getOne(findReview.getTransaction().getTransactionIdx());
        
        findReview.setDeleteYn(true);
        findReview.setUpdator(findUser.getEmail());
        reviewService.deleteReview(findReview);

        List<ReviewImage> imgs = reviewImageRepository.findByReview_ReviewIdx(findReview.getReviewIdx());
        for (int i = 0; i < imgs.size(); i++) {

            File file = new File(fileStorageLocation + File.separator + imgs.get(i).getImageUrl());

            if (file.exists()) {
                file.delete();
            }

            reviewImageRepository.delete(imgs.get(i));
        }

        //리뷰 별점 계산
        List<Review> reviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(transaction.getRental().getUser().getUserIdx(), true, false);
        for (int i = 0; i < reviews.size(); i++) {
            total += reviews.get(i).getReviewScore();
        }
        avg = total / reviews.size();
        starPoint = String.format("%.1f", avg);

        User starUser = userRepository.getOne(transaction.getRental().getUser().getUserIdx());
        starUser.setStarPoint(starPoint);
        userService.updateUser(starUser);

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getGradeInfoPage")
    public ResponseEntity<JsonObject> getGradeInfoPage(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray grArr = new JsonArray();

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        List<Likes> likes = likeRepository.findByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);

        Gson gson = new Gson();

        List<Grade> grades = gradeRepository.findByActiveYnAndMenuNumNotIn(true, new int[]{0});
        List<Grade> mains = gradeRepository.findByActiveYnAndMenuNumIn(true, new int[]{0});
        Grade grade = gradeRepository.getOne(findUser.getUserLevel());
        List<Rental> rentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYn(findUser.getUserIdx(), true, false);
        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());
        Grade getGrade = gradeRepository.findTop1ByOrderByGradeScoreDesc();

        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();

        for (int i = 0; i < grades.size(); i++) {
            linkedHashSet.add(grades.get(i).getGradeName().substring(2));
        }

        String[] resultArr = linkedHashSet.toArray(new String[0]);

        data.addProperty("imageUrl", findUser.getProfileImageUrl());
        data.addProperty("nickName", findUser.getNickName());
        data.addProperty("grade", grade.getGradeName()+grade.getMiddleGrade());
        data.addProperty("activityScore", findUser.getActivityScore());
        data.addProperty("maxScore", getGrade.getGradeScore());
        data.add("topSteps", gson.toJsonTree(resultArr));

        data.addProperty("boardCnt", rentals.size());
        data.addProperty("reviewCnt", userCount.getGiveReviewCnt());
        data.addProperty("likeCnt", likes.size());
        data.addProperty("viewCnt", userCount.getViewCnt());

        mains.forEach(
                main -> {
                    JsonArray subGrades = new JsonArray();
                    grades.forEach(
                            gr ->{
                                if (main.getGradeIdx().intValue() == gr.getMenuNum()) {
                                    JsonObject subObj = new JsonObject();
                                    subObj.addProperty("minorGrade", gr.getMiddleGrade());
                                    subObj.addProperty("gradeScore", gr.getGradeScore());

                                    subGrades.add(subObj);
                                }
                            }
                    );

                    JsonObject grInfo = new JsonObject();
                    grInfo.addProperty("majorGrade", main.getGradeName());
                    grInfo.addProperty("imageUrl", main.getImageUrl());
                    grInfo.add("minorInfo", subGrades);
                    grArr.add(grInfo);
                }
        );

        data.add("gradeInfos", grArr);

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


    @GetMapping("/getMyArbitrationList")
    public ResponseEntity<JsonObject> getMyArbitrationList(Pageable pageable,
                                                 HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray amArr = new JsonArray();
        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        AtomicReference<String> status = new AtomicReference<>("");

        Page<ArbitrationManagement> amList = arbitrationRepository.findAllByUser_userIdxAndDeleteYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);
        List<ArbitrationManagement> amSize = arbitrationRepository.findAllByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);

        if (amList.getSize() != 0) {
            amList.forEach(
                    ams -> {
                        JsonObject am = new JsonObject();

                        am.addProperty("amIdx", ams.getAmIdx());

                        switch (ams.getAmStatus()) {
                            case 0:
                                status.set("신청완료");
                                break;
                            case 1:
                                status.set("중재중");
                                break;
                            default:
                                status.set("중재완료");
                                break;
                        }

                        am.addProperty("amStatus", status.get());
                        am.addProperty("regDate", ams.getCreateAt().getTime());
                        am.addProperty("content", ams.getAmContent());

                        amArr.add(am);
                    }
            );
        }


        data.add("arbitrations", amArr);
        data.addProperty("totalCount", amSize.size());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }
}
