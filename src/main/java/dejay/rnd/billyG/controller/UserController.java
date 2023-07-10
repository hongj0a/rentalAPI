package dejay.rnd.billyG.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.config.ImageProperties;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.*;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repositoryImpl.UserCountRepositories;
import dejay.rnd.billyG.service.*;
import dejay.rnd.billyG.util.FrontUtil;
import dejay.rnd.billyG.service.UserMining;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
    private final AmImageRepository amImageRepository;
    private final ArbitrationService arbitrationService;
    private final ToBlockRepository toBlockRepository;
    private final BlockReviewRepository blockReviewRepository;
    private final PushService pushService;
    private final TransactionService transactionService;
    private final UserCountService userCountService;
    private final UserMining userMining;
    private final KmsService kmsService;

    public UserController(ImageProperties imageProperties, UserService userService, UserRepository userRepository, TownService townService, TownRepository townRepository, FileUploadService uploadService, RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository, GradeRepository gradeRepository, CategoryRepository categoryRepository, StatusHistoryRepository statusHistoryRepository, TransactionRepository transactionRepository, LikeRepository likeRepository, UserCountRepository userCountRepository, UserCountRepositories userCountRepositories, ReviewService reviewService, UserEvaluationRepository userEvaluationRepository, ArbitrationRepository arbitrationRepository, AmImageRepository amImageRepository, ArbitrationService arbitrationService, ToBlockRepository toBlockRepository, BlockReviewRepository blockReviewRepository, PushService pushService, TransactionService transactionService, UserCountService userCountService, UserMining userMining, KmsService kmsService) {
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
        this.userMining = userMining;
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
        this.amImageRepository = amImageRepository;
        this.arbitrationService = arbitrationService;

        this.toBlockRepository = toBlockRepository;
        this.blockReviewRepository = blockReviewRepository;
        this.pushService = pushService;
        this.transactionService = transactionService;
        this.userCountService = userCountService;
        this.kmsService = kmsService;
    }

    @PostMapping("/editProfile")
    public ResponseEntity<JsonObject> editProfile(@RequestPart(value="image", required = false) MultipartFile multipartFile,
            @RequestParam(value="nickName", required = false) String nickName,
                                                  HttpServletRequest req) throws ParseException, IOException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);
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
        User findUser = userMining.getUserInfo(acToken);

        Map<Integer, Long> userTowns = new HashMap<>();
        Town userTownIdx = townService.setTowns(userTown.getLeadTownName());
        userTowns.put(0, userTownIdx.getTownIdx());

        for (int k = 1; k <= 4; k++) {
            userTowns.put(k, null);
        }
        userService.updateUserTownInfo(userTowns, findUser);

        if (userTown.getTowns().length > 0) {
            for (int i = 0; i < userTown.getTowns().length; i++) {
                if(!StringUtils.isEmpty(userTown.getTowns()[i])) {
                    //townService.setUserTownInfo(findUser.getUserIdx(), userTown.getTowns()[i], false);
                    //town테이블에 있는 타운인지 조회해서 인덱스 리턴받고 배열에 저장
                    userTownIdx = townService.setTowns(userTown.getTowns()[i]);
                    userTowns.put(i+1, userTownIdx.getTownIdx());
                }
            }
        } else if (userTown.getTowns().length == 0) {
            for (int j = 0; j < 4; j++) {
                userTowns.put(j + 1, null);
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
        User findUser = userMining.getUserInfo(acToken);

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

        //List<User> findUser = userService.findByNickName(nickName);
        List<User> findUser = userRepository.findAllByNickName(nickName);

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
        User findUser = userMining.getUserInfo(acToken);

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
       // User otherUser;

        String acToken = req.getHeader("Authorization").substring(7);
        User otherUser = userMining.getUserInfo(acToken);

        if (userIdx != 0 && userIdx != otherUser.getUserIdx()) {
            ToBlock findBlockInfo = toBlockRepository.findByUser_userIdxAndBlockUser_userIdxAndDeleteYn(otherUser.getUserIdx(), userIdx, false);

            if (findBlockInfo == null) {
                //block 정보 없음 block 가능
                data.addProperty("blockYn", true);
            } else {
                //block 정보 있음 block 불가능
                data.addProperty("blockYn", false);
            }

            otherUser = userRepository.getOne(userIdx);

            data.addProperty("isMine", false);


        } else if (userIdx == 0 || userIdx == otherUser.getUserIdx()) {
            data.addProperty("isMine", true);
        }

        Grade grade = gradeRepository.findTop1ByOrderByGradeScoreDesc();
        Grade findGrade = gradeRepository.getOne(otherUser.getUserLevel());
        Grade gName = gradeRepository.getOne(Long.valueOf(findGrade.getMenuNum()));

        if (otherUser.getLeadTown() != null) {
            Town findLeadTown = townRepository.getOne(otherUser.getLeadTown());
            data.addProperty("leadTown", findLeadTown.getTownName());
        } else {
            data.addProperty("leadTown", "");
        }


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
        data.addProperty("name", kmsService.decrypt(otherUser.getName()));
        data.addProperty("status", otherUser.getStatus());
        data.addProperty("phoneNumber", kmsService.decrypt(otherUser.getPhoneNum()));
        data.addProperty("snsType", otherUser.getSnsName());
        data.addProperty("grade", gName.getGradeName());
        data.addProperty("email", otherUser.getEmail());
        data.addProperty("idEmail", otherUser.getIdEmail());
        data.addProperty("activityScore", otherUser.getActivityScore());
        data.addProperty("maxScore", grade.getGradeScore());
        data.addProperty("starPoint", Float.parseFloat(otherUser.getStarPoint()));
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

        String acToken = req.getHeader("Authorization").substring(7);
        User loginUser = userMining.getUserInfo(acToken);;
        User otherUser;

        if (userIdx != 0) {
            otherUser = userRepository.getOne(userIdx);
        } else {

            otherUser = userRepository.getOne(loginUser.getUserIdx());
        }

        if (type == 1) {
            //렌탈중인 게시글
            Page<Rental> etcRentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotInOrderByCreateAtDesc(otherUser.getUserIdx(), true, false, new int[]{4}, pageable);
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
                        BlockReview blockReview;
                        Town findLeadTown = townRepository.getOne(review.getTransaction().getUser().getLeadTown());

                        rvs.addProperty("renterIdx", review.getTransaction().getUser().getUserIdx());
                        rvs.addProperty("renterImage", review.getTransaction().getUser().getProfileImageUrl());
                        if (review.getRenterIdx() == loginUser.getUserIdx()) {
                            rvs.addProperty("isMine", true);
                        } else {
                            rvs.addProperty("isMine", false);
                        }
                        rvs.addProperty("renterNickName", review.getTransaction().getUser().getNickName());
                        rvs.addProperty("renterLeadTown", findLeadTown.getTownName());
                        rvs.addProperty("reviewRegDate", review.getCreateAt().getTime());
                        rvs.addProperty("reviewIdx", review.getReviewIdx());
                        rvs.addProperty("status", review.getTransaction().getRental().getUser().getStatus());
                        rvs.addProperty("reviewContent", review.getReviewContent());
                        rvs.addProperty("reviewStarPoint", review.getReviewScore());

                        blockReview = blockReviewRepository.findByReporterIdxAndReview_reviewIdxAndProcessingStatusNotIn(loginUser.getUserIdx(), review.getReviewIdx(), new int[]{2});

                        if (blockReview != null) {
                            rvs.addProperty("blockReviewHistory", true);
                        } else if (blockReview == null){
                            rvs.addProperty("blockReviewHistory", false);
                        }

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
                        Town findLeadTown = townRepository.getOne(review.getTransaction().getUser().getLeadTown());

                        rvs.addProperty("renterIdx", review.getTransaction().getUser().getUserIdx());
                        List<RentalImage> images = rentalImageRepository.findByRental_rentalIdx(review.getTransaction().getRental().getRentalIdx());
                        rvs.addProperty("renterImage", images.get(0).getImageUrl());

                        if (review.getRenterIdx() == loginUser.getUserIdx()) {
                            rvs.addProperty("isMine", true);
                        } else {
                            rvs.addProperty("isMine", false);
                        }
                        rvs.addProperty("renterNickName", review.getTransaction().getRental().getTitle());
                        rvs.addProperty("renterLeadTown", findLeadTown.getTownName());
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
        User findUser = userMining.getUserInfo(acToken);

        //삭제검사 생략, 애초에 리뷰 리스트 가져올 때 delete_yn 필터링 됨.

        Review review = reviewRepository.getOne(reviewIdx);

        BlockReview blockReview = blockReviewRepository.findByReporterIdxAndReview_reviewIdxAndProcessingStatusNotIn(findUser.getUserIdx(), review.getReviewIdx(), new int[]{2});


        if (findUser.getUserIdx() == review.getRenterIdx()) {
            data.addProperty("isMine", true);
        } else {
            data.addProperty("isMine", false);
        }

        if (blockReview != null) {
            data.addProperty("blockReviewHistory", true);
        } else if (blockReview == null){
            data.addProperty("blockReviewHistory", false);
        }

        data.addProperty("userIdx", review.getTransaction().getUser().getUserIdx());
        data.addProperty("nickName", review.getTransaction().getUser().getNickName());
        data.addProperty("status", review.getTransaction().getRental().getUser().getStatus());
        data.addProperty("starPoint", review.getReviewScore());
        data.addProperty("title", review.getTransaction().getRental().getTitle());
        data.addProperty("content", review.getReviewContent());
        data.addProperty("transactionNum", review.getTransaction().getTransactionNum());

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

        }
        data.add("reviewImageList", rvImg);
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
        User findUser = userMining.getUserInfo(acToken);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        data.addProperty("chatNoticeYn", findUser.isChatNoticeYn());
        data.addProperty("marketingNoticeYn", findUser.isMarketingNoticeYn());
        data.addProperty("activityNoticeYn", findUser.isActivityNoticeYn());
        data.addProperty("noticeNoticeYn", findUser.isNoticeNoticeYn());
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/setEmail")
    public ResponseEntity<JsonObject> setEmail(@RequestBody UserDto userDto,
                                                  HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        findUser.setIdEmail(userDto.getEmail());
        userService.updateUser(findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());


    }

    @PostMapping("/setDoNotDisturb")
    public ResponseEntity<JsonObject> setDoNotDisturb(@RequestBody AlarmDto alarmDto,
                                               HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        findUser.setDoNotDisturbTimeYn(alarmDto.isDoNotDisturbTimeYn());
        if (alarmDto.isDoNotDisturbTimeYn() == true) {
            findUser.setDoNotDisturbStartHour(alarmDto.getStartHour());
            findUser.setDoNotDisturbStartMinute(alarmDto.getStartMinute());
            findUser.setDoNotDisturbEndHour(alarmDto.getEndHour());
            findUser.setDoNotDisturbEndMinute(alarmDto.getEndMinute());
        }

        findUser.setUpdator(findUser.getEmail());
        userService.updateUser(findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @PostMapping("/setAlarm")
    public ResponseEntity<JsonObject> setAlarm(@RequestBody AlarmDto alarmDto,
                                               HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        findUser.setChatNoticeYn(alarmDto.isChatNoticeYn());
        findUser.setActivityNoticeYn(alarmDto.isActivityNoticeYn());
        findUser.setMarketingNoticeYn(alarmDto.isMarketingNoticeYn());
        findUser.setNoticeNoticeYn(alarmDto.isNoticeNoticeYn());

        findUser.setUpdator(findUser.getEmail());
        userService.updateUser(findUser);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }



    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/setWithDraw")
    public ResponseEntity<JsonObject> setWithDraw(@RequestBody OutDto outDto,
                                                  HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);
        Category findCategory = categoryRepository.getOne(outDto.getTypeIdx());

        findUser.setCategory(findCategory);
        findUser.setStatus(30);
        findUser.setDeleteAt(FrontUtil.getNowDate());
        if (outDto.getBlockContent() != null) {
            findUser.setOutReason(outDto.getBlockContent());
        }
        findUser.setUpdator(findUser.getEmail());
        userService.updateUser(findUser);

        StatusHistory sh = new StatusHistory();
        sh.setUser(findUser);
        sh.setDeleteAt(FrontUtil.getNowDate());
        sh.setCreateAt(FrontUtil.getNowDate());
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
        statusMap.put(30, "매칭완료");
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
        User findUser = userMining.getUserInfo(acToken);

        Page<Transaction> transactions;
        List<Transaction> tranSize;

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
                    trs.addProperty("rentalStatus", tr.getRental().getStatus());
                    List<RentalImage> img = rentalImageRepository.findByRental_rentalIdx(tr.getRental().getRentalIdx());
                    if (img.size() != 0) {
                        trs.addProperty("imageSeq", img.get(0).getImageIdx());
                        trs.addProperty("imageUrl", img.get(0).getImageUrl());
                    }
                    trs.addProperty("title", tr.getRentalHistory().getTitle());

                    trs.addProperty("status", tr.getOwnerStatus());
                    Review findReview = reviewRepository.findByTransaction_TransactionIdxAndTransaction_OwnerStatus(tr.getTransactionIdx(), 70);
                    if (findReview != null) {
                        trs.addProperty("canFlag", false);
                    } else {
                        trs.addProperty("canFlag", true);
                    }
                    trs.addProperty("dailyRentalFee", tr.getRentalHistory().getRentalPrice());

                    renArr.add(trs);
                }
        );

        data.add("transactions", renArr);
        data.addProperty("totalCount", tranSize.size());

        System.out.println("apiRes:"+apiRes);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getLikeList")
    public ResponseEntity<JsonObject> getLikeList(Pageable pageable,
                                                  HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray myArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        Gson gson = new Gson();
        Page<Likes> likes = likeRepository.findByUser_userIdxAndDeleteYnAndRental_statusNotInOrderByRental_PullUpAtDesc(findUser.getUserIdx(), false, new int[]{4}, pageable);
        List<Likes> likeSize = likeRepository.findByUser_userIdxAndDeleteYnAndRental_statusNotIn(findUser.getUserIdx(), false, new int[]{4} );

        List<ToBlock> findBlock = toBlockRepository.findByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);

        ArrayList<Long> blocks = new ArrayList<>();
        for (int i = 0; i < findBlock.size(); i++) {
            blocks.add(findBlock.get(i).getBlockUser().getUserIdx());
        }
        ArrayList<Long> sizes = new ArrayList<>();
        for (int j = 0; j < likeSize.size(); j++) {
            if (!blocks.contains(likeSize.get(j).getRental().getUser().getUserIdx())) {
                sizes.add(likeSize.get(j).getLikeIdx());
            }
        }

        likes.forEach(
                li -> {
                    if (!blocks.contains(li.getRental().getUser().getUserIdx())) {
                        JsonObject my = new JsonObject();
                        boolean rentalStatus;

                        my.addProperty("rentalSeq", li.getRental().getRentalIdx());
                        my.addProperty("title", li.getRental().getTitle());
                        my.addProperty("userNickName", li.getRental().getUser().getNickName());


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
                        my.addProperty("regDate", li.getRental().getPullUpAt().getTime());
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

                }
        );

        data.add("myLikes", myArr);
        data.addProperty("totalCount", sizes.size());
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
                                                HttpServletRequest req) throws AppException, ParseException, IOException {
        JsonObject data = new JsonObject();
        Executor executor = Executors.newFixedThreadPool(30);
        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

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
            userCountService.updateCnt(userCount);

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

        if (images.size() != 0 && images != null) {
            for (int i = 0; i < images.size(); i++) {
                if (!StringUtils.isEmpty(images.get(i).getOriginalFilename())) {
                    ReviewImage reviewImage = new ReviewImage();
                    ImageFile file = uploadService.upload(images.get(i));

                    reviewImage.setReview(getReview);
                    reviewImage.setImageUrl(file.getFileName());

                    reviewImageRepository.save(reviewImage);
                }


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


        CompletableFuture.runAsync(() -> {
            try {
                pushService.sendPush(new Long[]{transaction.getRental().getUser().getUserIdx()}, findUser.getUserIdx(),
                        transaction.getRental().getRentalIdx(), 0L,20,"새로운 평가 등록", findUser.getNickName()+"님이 회원님의 "+transaction.getRental().getTitle()+" 게시물에 대한 새로운 평가를 등록하였습니다.");

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": hi");
        }, executor);


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
        User findUser = userMining.getUserInfo(acToken);
        
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
        User findUser = userMining.getUserInfo(acToken);
        List<Likes> likes = likeRepository.findByUser_userIdxAndDeleteYnAndRental_statusNotIn(findUser.getUserIdx(), false, new int[]{4});

        Gson gson = new Gson();

        List<Grade> grades = gradeRepository.findByActiveYnAndMenuNumNotIn(true, new int[]{0});
        List<Grade> mains = gradeRepository.findByActiveYnAndMenuNumIn(true, new int[]{0});
        Grade grade = gradeRepository.getOne(findUser.getUserLevel());
        Grade gName = gradeRepository.getOne(Long.valueOf(grade.getMenuNum()));
        List<Rental> rentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYn(findUser.getUserIdx(), true, false);
        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());
        Grade getGrade = gradeRepository.findTop1ByOrderByGradeScoreDesc();

        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();

        for (int i = 0; i < mains.size(); i++) {
            linkedHashSet.add(mains.get(i).getGradeName().substring(2));
        }

        String[] resultArr = linkedHashSet.toArray(new String[0]);

        data.addProperty("imageUrl", findUser.getProfileImageUrl());
        data.addProperty("nickName", findUser.getNickName());
        data.addProperty("grade", gName.getGradeName()+grade.getMiddleGrade());
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
        User findUser = userMining.getUserInfo(acToken);
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
                            case 2:
                                status.set("중재완료");
                                break;
                            default:
                                //예외처리
                                break;
                        }

                        am.addProperty("amStatus", status.get());
                        am.addProperty("transactionIdx", ams.getTransaction().getTransactionIdx());
                        am.addProperty("regDate", ams.getCreateAt().getTime());
                        am.addProperty("content", ams.getTransaction().getRentalHistory().getTitle());

                        amArr.add(am);
                    }
            );
        }


        data.add("arbitrations", amArr);
        data.addProperty("totalCount", amSize.size());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getMyArbitration")
    public ResponseEntity<JsonObject> getMyArbitration(@RequestParam(value="amIdx") Long amIdx,
                                                @RequestParam(value = "transactionIdx") Long transactionIdx,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray imageArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Transaction getTr = transactionRepository.getOne(transactionIdx);

        Rental findRental = rentalRepository.getOne(getTr.getRental().getRentalIdx());
        ArbitrationManagement findAM = arbitrationRepository.getOne(amIdx);
        List<AmImage> amImages = amImageRepository.findByArbitrationManagement_AmIdx(amIdx);
        List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(getTr.getRental().getRentalIdx());

        if (amImages.size() != 0) {
            amImages.forEach(
                    img -> {
                        JsonObject images = new JsonObject();
                        images.addProperty("imageSeq", img.getImageIdx());
                        images.addProperty("imageUrl", img.getImageUrl());

                        imageArr.add(images);
                    }
            );
            data.add("arbitrationImages", imageArr);
        }


        data.addProperty("rentalSeq", findRental.getRentalIdx());
        if (rentalImages.size() != 0) {
            data.addProperty("rentalImage", rentalImages.get(0).getImageUrl());
        }

        data.addProperty("title", findRental.getTitle());
        data.addProperty("transactionNum", getTr.getTransactionNum());
        data.addProperty("dailyFee", findRental.getRentalPrice());
        data.addProperty("renterNickName", getTr.getUser().getNickName());
        data.addProperty("arbitrationContent", findAM.getAmContent());
        data.addProperty("regDate", findAM.getCreateAt().getTime());


        if (findAM.getAmStatus() == 2) {
            data.addProperty("arbitrationStatus", "중재완료");
            data.addProperty("arbitrationAnswer", StringEscapeUtils.unescapeHtml4(findAM.getAnswerContent()));
        } else if (findAM.getAmStatus() == 1){
            data.addProperty("arbitrationStatus", "중재중");
            data.addProperty("arbitrationAnswer", "");
        } else {
            data.addProperty("arbitrationStatus", "신청완료");
            data.addProperty("arbitrationAnswer", "");
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/delArbitration")
    public ResponseEntity<JsonObject> delArbitration(HttpServletRequest req,
                                                   @RequestBody ArbitrationDto rmDto) throws ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        ArbitrationManagement findRM = arbitrationRepository.getOne(rmDto.getAmIdx());

        findRM.setDeleteYn(true);
        findRM.setUpdator(findUser.getEmail());
        arbitrationService.deleteRM(findRM);

        List<AmImage> imgs = amImageRepository.findByArbitrationManagement_AmIdx(rmDto.getAmIdx());
        for (int i = 0; i < imgs.size(); i++) {

            File file = new File(fileStorageLocation + File.separator + imgs.get(i).getImageUrl());

            if (file.exists()) {
                file.delete();
            }

            amImageRepository.delete(imgs.get(i));
        }


        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }
    @GetMapping("/getArbitration")
    public ResponseEntity<JsonObject> getArbitration(@RequestParam(value = "transactionIdx") Long transactionIdx,
                                                       HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Transaction getTr = transactionRepository.findByTransactionIdx(transactionIdx);

        Rental findRental = rentalRepository.getOne(getTr.getRental().getRentalIdx());
        List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(getTr.getRental().getRentalIdx());

        data.addProperty("rentalSeq", findRental.getRentalIdx());
        if (rentalImages.size() != 0) {
            data.addProperty("rentalImage", rentalImages.get(0).getImageUrl());
        }

        data.addProperty("title", findRental.getTitle());
        data.addProperty("transactionNum", getTr.getTransactionNum());
        data.addProperty("transactionIdx", getTr.getTransactionIdx());
        data.addProperty("dailyFee", findRental.getRentalPrice());
        data.addProperty("renterNickName", getTr.getUser().getNickName());
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/setArbitration", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> setArbitration(@RequestParam (value = "images", required = false) List<MultipartFile> images,
                                                @RequestParam (value = "content") String content,
                                                @RequestParam (value = "transactionIdx") Long transactionIdx,
                                                HttpServletRequest req) throws AppException, ParseException, IOException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Transaction findTr = transactionRepository.findByTransactionIdx(transactionIdx);

        ArbitrationManagement am = new ArbitrationManagement();
        am.setTransaction(findTr);
        am.setUser(findUser);
        am.setAmContent(content);
        am.setAmStatus(0);

        ArbitrationManagement arbitrationManagement = arbitrationRepository.save(am);

        for (int i = 0; i < images.size(); i++) {
            if (!StringUtils.isEmpty(images.get(i).getOriginalFilename())) {
                AmImage amImage = new AmImage();
                ImageFile file = uploadService.upload(images.get(i));

                amImage.setArbitrationManagement(arbitrationManagement);
                amImage.setImageUrl(file.getFileName());

                amImageRepository.save(amImage);
            }
        }

        findTr.setOwnerStatus(60);
        findTr.setRenterStatus(60);
        transactionService.updateTransacion(findTr);

        TransactionHistory th = new TransactionHistory();
        th.setOwnerStatus(60);
        th.setRenterStatus(60);
        transactionService.insertHistory(th);

        Executor executor = Executors.newFixedThreadPool(30);


        CompletableFuture.runAsync(() -> {
            try {
                //렌탈오너에게
                pushService.sendPush(new Long[]{findTr.getRental().getUser().getUserIdx()}, findTr.getUser().getUserIdx(), arbitrationManagement.getAmIdx(),
                        0L,60, "이의신청 접수완료", findTr.getUser().getNickName()+" 님과의 렌탈거래에 대한 이의신청이 접수되었습니다.");

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": hi");
        }, executor);




        CompletableFuture.runAsync(() -> {
            try {
                //렌탈러에게
                pushService.sendPush(new Long[]{findTr.getUser().getUserIdx()}, findTr.getRental().getUser().getUserIdx(), arbitrationManagement.getAmIdx(),
                        0L,60, "이의신청으로 인한 거래중재중", findTr.getRental().getTitle()+ " 거래에 대해 렌탈오너가 이의를 제기 했습니다. ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": hi");
        }, executor);


        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


    //나의 차단유저리스트 불러오기
    //게시글 차단하면 게시글상세 접근불가, 제3자 프로필 접근불가
    //채팅목록에서는 보일 수 있으나
    //채팅상세에서 차단유저인지 플래그 리턴해주면 제3자프로필로 접근못함
    //채팅 못함, 채팅내용은 확인할 수 있어야함
    //채팅방내에서 차단플래그로 구분, 거래중일 땐 차단 못함
    //채팅방내에 거래중인지 리턴플래그 필요
    @GetMapping("/getMyBlockUserList")
    public ResponseEntity<JsonObject> getMyBlockUserList(Pageable pageable,HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray buArr = new JsonArray();

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        List<ToBlock> total = toBlockRepository.findByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);
        Page<ToBlock> blocks = toBlockRepository.findByUser_userIdxAndDeleteYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);

        blocks.forEach(
                block -> {
                    JsonObject bl = new JsonObject();

                    bl.addProperty("toBlockIdx", block.getToBlockIdx());
                    bl.addProperty("toBlockUserIdx", block.getBlockUser().getUserIdx());
                    bl.addProperty("toBlockUserNickName", block.getBlockUser().getNickName());
                    bl.addProperty("toBlockUserImage", block.getBlockUser().getProfileImageUrl());

                    buArr.add(bl);
                }
        );

        data.add("personalBlocks", buArr);
        data.addProperty("totalCount", total.size());

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


}
