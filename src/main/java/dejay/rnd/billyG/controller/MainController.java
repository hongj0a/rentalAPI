package dejay.rnd.billyG.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.repositoryImpl.RentalRepositories;
import dejay.rnd.billyG.repositoryImpl.TownRepositories;
import dejay.rnd.billyG.service.AlarmService;
import dejay.rnd.billyG.service.CategoryService;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.RentalService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalTime.now;

@RestController
@RequestMapping("/api")
public class MainController {
    private final UserRepository userRepository;
    private final TownRepository townRepository;
    private final TownRepositories townRepositories;
    private final CategoryService categoryService;
    private final RentalRepository rentalRepository;
    private final RentalRepositories rentalRepositories;
    private final RentalImageRepository rentalImageRepository;
    private final RentalCategoryInfoRepository rentalCategoryInfoRepository;
    private final RentalService rentalService;
    private final TransactionRepository transactionRepository;
    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final AlarmService alarmService;
    private final GradeRepository gradeRepository;
    private final FileUploadService uploadService;
    private final CategoryRepository categoryRepository;

    public MainController(UserRepository userRepository, TownRepository townRepository, TownRepositories townRepositories, CategoryService categoryService, RentalRepository rentalRepository, RentalRepositories rentalRepositories, RentalImageRepository rentalImageRepository, RentalCategoryInfoRepository rentalCategoryInfoRepository, RentalService rentalService, TransactionRepository transactionRepository, LikeRepository likeRepository, AlarmRepository alarmRepository, ReviewRepository reviewRepository, AlarmService alarmService, GradeRepository gradeRepository, FileUploadService uploadService, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.townRepository = townRepository;
        this.townRepositories = townRepositories;
        this.categoryService = categoryService;
        this.rentalRepository = rentalRepository;
        this.rentalRepositories = rentalRepositories;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalCategoryInfoRepository = rentalCategoryInfoRepository;
        this.rentalService = rentalService;
        this.transactionRepository = transactionRepository;
        this.likeRepository = likeRepository;
        this.alarmRepository = alarmRepository;
        this.reviewRepository = reviewRepository;
        this.alarmService = alarmService;
        this.gradeRepository = gradeRepository;
        this.uploadService = uploadService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/string")
    public String test() {
        return "hello";
    }
//
    @GetMapping("/getMainList")
    public ResponseEntity<JsonObject> getMain(@RequestParam(value="status") Integer status,
                                              @RequestParam(required = false, value="filter") Integer filter,
                                              @RequestParam(required = false, value="keyword") String keyword,
                                              @RequestParam(required = false, value="categories") Long[] categories,
                                              @RequestParam(required = false, value="towns") Long[] towns, Pageable pageable,
                                              HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray rentalArr = new JsonArray();

        /**
         * [렌탈 상태]
         * 렌탈가능 [Enable(1)]
         * 렌탈중 [ing(2)]
         * 렌탈완료 [complete(3)]
         * 렌탈숨기기 [hide(4)]
         * =================================
         * 파라미터는 렌탈가능(1) or 렌탈완료(2)
         * if status 전체(0) == 1,2,3
         * else if status 렌탈가능(1) == 1
         * else status 렌탈완료(2) == 2,3
         */

        ArrayList<Integer> p_status = new ArrayList<>();

        if (status == 0) {
            p_status.add(1);
            p_status.add(2);
            p_status.add(3);
        } else if (status == 1) {
            p_status.add(1);
        } else {
            p_status.add(2);
            p_status.add(3);
        }


        Integer totalCount = rentalRepositories.getTotalCount(p_status, keyword, towns, categories);

        Page<Rental> mains = rentalRepositories.findAll(p_status, filter, keyword, towns, categories, pageable);
        mains.forEach(
                rental -> {
                    JsonObject rentalList = new JsonObject();
                    rentalList.addProperty("rentalSeq", rental.getRentalIdx());

                    //썸네일추출
                    List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(rental.getRentalIdx());
                    if (rentalImages.size() != 0) {
                        rentalList.addProperty("imageUrl", rentalImages.get(0).getImageUrl());
                    }

                    rentalList.addProperty("title", rental.getTitle());
                    rentalList.addProperty("regDate", rental.getCreateAt().getTime());
                    rentalList.addProperty("dailyRentalFee", rental.getRentalPrice());

                    //town 리스트 추출
                    List<String> tLst = new ArrayList<>();

                    if (null != rental.getLeadTown()) {
                        Town Ltown = townRepository.getOne(rental.getLeadTown());
                        tLst.add(Ltown.getTownName());
                    }

                    if ( null != rental.getTown1() ) {
                        Town town1 = townRepository.getOne(rental.getTown1());
                        tLst.add(town1.getTownName());

                    }

                    if ( null != rental.getTown2() ) {
                        Town town2 = townRepository.getOne(rental.getTown2());
                        tLst.add(town2.getTownName());
                    }

                    if ( null != rental.getTown3() ) {
                        Town town3 = townRepository.getOne(rental.getTown3());
                        tLst.add(town3.getTownName());
                    }

                    if ( null != rental.getTown4() ) {
                        Town town4 = townRepository.getOne(rental.getTown4());
                        tLst.add(town4.getTownName());
                    }

                    Gson gson = new Gson();
                    rentalList.add("towns", gson.toJsonTree(tLst));

                    rentalArr.add(rentalList);
                }
        );
        data.add("rentals", rentalArr);
        data.addProperty("totalCount", totalCount);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getStatus")
    public ResponseEntity<JsonObject> getStatus(HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray statusArr = new JsonArray();

        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(1, "렌탈가능");
        statusMap.put(2, "렌탈완료");

        for (Map.Entry<Integer, String> pair : statusMap.entrySet()) {
            JsonObject status = new JsonObject();

            status.addProperty("statusKey", pair.getKey());
            status.addProperty("status", pair.getValue());

            statusArr.add(status);
        }

        data.add("statusList", statusArr);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getCategory")
    public ResponseEntity<JsonObject> getCategory(HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray categoryArr = new JsonArray();

        List<Category> categoryList = categoryService.findAllN();

        categoryList.forEach(
                category -> {
                    JsonObject categories = new JsonObject();
                    categories.addProperty("categorySeq", category.getCategoryIdx());
                    categories.addProperty("categoryName", category.getName());
                    if (category.getOnImageUrl() == null) {
                        categories.addProperty("categoryOnImage", "");
                    } else {
                        categories.addProperty("categoryOnImage", category.getOnImageUrl());
                    }

                    if (category.getOffImageUrl() == null) {
                        categories.addProperty("categoryOffImage", "");
                    } else {
                        categories.addProperty("categoryOffImage", category.getOffImageUrl());
                    }

                    categoryArr.add(categories);
                }
        );
        data.add("categoryList", categoryArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getTowns")
    public ResponseEntity<JsonObject> getTowns(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        ArrayList<Long> towns = new ArrayList<>();

        towns.add(findUser.getLeadTown());
        if (findUser.getTown1()!= null) towns.add(findUser.getTown1());
        if (findUser.getTown2()!= null) towns.add(findUser.getTown2());
        if (findUser.getTown3()!= null) towns.add(findUser.getTown3());
        if (findUser.getTown4()!= null) towns.add(findUser.getTown4());

        List<Town> town = townRepositories.findByTownInfo(towns);

        town.forEach(
                tn -> {
                    JsonObject tns = new JsonObject();
                    tns.addProperty("townSeq", tn.getTownIdx());
                    tns.addProperty("townName", tn.getTownName());

                    townArr.add(tns);
                }
        );
        data.add("townList", townArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getRental")
    public ResponseEntity<JsonObject> getRental(@RequestParam(value="rentalIdx") Long rentalIdx,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();
        JsonArray imageArr = new JsonArray();
        JsonArray cateArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Grade getGrade = gradeRepository.findTop1ByOrderByGradeScoreDesc();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(rentalIdx);
        List<Review> reviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAt(findRental.getUser().getUserIdx(), true, false);

        if (findRental.isDeleteYn() == true || findRental.getStatus() == 4 || findRental.isActiveYn() == false) {
            apiRes.setError(ErrCode.err_api_is_deleted_post.code());
            apiRes.setMessage(ErrCode.err_api_is_deleted_post.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

        if (findRental.getUser().getProfileImageUrl() != null) {
            data.addProperty("userProfileImage", findRental.getUser().getProfileImageUrl());
        } else {
            data.addProperty("userProfileImage", "");
        }


        data.addProperty("userNickName", findRental.getUser().getNickName());
        data.addProperty("userIdx", findRental.getUser().getUserIdx());
        data.addProperty("userStarPoint", findRental.getUser().getStarPoint());
        data.addProperty("activityScore", findRental.getUser().getActivityScore());
        data.addProperty("userGrade", findRental.getUser().getUserLevel());
        data.addProperty("activeScore", findRental.getUser().getActivityScore());
        data.addProperty("maxScore", getGrade.getGradeScore());
        data.addProperty("reviewCount", reviews.size());

        if (findRental.getUser().getUserIdx() != findUser.getUserIdx()) {
            //조회수 업데이트
            rentalService.updateViewCnt(findRental);
            data.addProperty("isMine",false);
        } else {
            data.addProperty("isMine", true);
        }

        //좋아요 눌렀는지 안 눌렀는지 플래그 값
        Likes likeFlag = likeRepository.findByRental_rentalIdxAndUser_userIdxAndDeleteYn(rentalIdx, findUser.getUserIdx(), false);

        if (likeFlag != null) {
            data.addProperty("likeFlag", true);
        } else {
            data.addProperty("likeFlag", false);
        }

        String status;
        if (findRental.getStatus() == 1) {
            status = "렌틸가능";
        } else if (findRental.getStatus() == 2) {
            status = "렌탈중";
        } else {
            status = "렌탈완료";
        }

        data.addProperty("rentalIdx", findRental.getRentalIdx());
        data.addProperty("rentalStatus", status);
        data.addProperty("viewCount", findRental.getViewCnt());
        data.addProperty("likeCount", findRental.getLikeCnt());
        data.addProperty("title", findRental.getTitle());
        data.addProperty("content", findRental.getContent());
        data.addProperty("createAt", findRental.getCreateAt().getTime());
        data.addProperty("dailyFee", findRental.getRentalPrice());

        ArrayList<Long> towns = new ArrayList<>();

        towns.add(findRental.getLeadTown());
        if (findRental.getTown1()!= null) towns.add(findRental.getTown1());
        if (findRental.getTown2()!= null) towns.add(findRental.getTown2());
        if (findRental.getTown3()!= null) towns.add(findRental.getTown3());
        if (findRental.getTown4()!= null) towns.add(findRental.getTown4());

        List<Town> town = townRepositories.findByTownInfo(towns);

        town.forEach(
                tn -> {
                    JsonObject tns = new JsonObject();
                    tns.addProperty("townSeq", tn.getTownIdx());
                    tns.addProperty("townName", tn.getTownName());

                    townArr.add(tns);
                }
        );

        List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(rentalIdx);

        rentalImages.forEach(
                img -> {
                    JsonObject images = new JsonObject();
                    images.addProperty("imageSeq", img.getImageIdx());
                    images.addProperty("imageUrl", img.getImageUrl());

                    imageArr.add(images);
                }
        );

        List<RentalCategoryInfo> categoryInfos = rentalCategoryInfoRepository.findByRental_rentalIdx(rentalIdx);

        categoryInfos.forEach(
                cate -> {
                    JsonObject categories = new JsonObject();
                    categories.addProperty("categoryIdx", cate.getCategory().getCategoryIdx());
                    categories.addProperty("categoryName", cate.getCategory().getName());

                    cateArr.add(categories);
                }
        );

        data.add("towns", townArr);
        data.add("images", imageArr);
        data.add("categoryInfo", cateArr);

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getEtcRentals")
    public ResponseEntity<JsonObject> getEtcRentals(@RequestParam(value="rentalIdx") Long rentalIdx, Pageable pageable,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray renArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(rentalIdx);


        Page<Rental> etcRentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotIn(findRental.getUser().getUserIdx(), true, false, new int[]{4}, pageable);
        List<Rental> etc = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotIn(findRental.getUser().getUserIdx(), true, false, new int[]{4});

        etcRentals.forEach(
                etcs -> {
                    JsonObject etcRental = new JsonObject();

                    List<RentalImage> renImgs = rentalImageRepository.findByRental_rentalIdx(etcs.getRentalIdx());

                    etcRental.addProperty("rentalIdx", etcs.getRentalIdx());
                    etcRental.addProperty("rentalImage", renImgs.get(0).getImageUrl());
                    etcRental.addProperty("title", etcs.getTitle());
                    etcRental.addProperty("dailyFee", etcs.getRentalPrice());

                    renArr.add(etcRental);
                }
        );

        data.add("etcRentals", renArr);
        data.addProperty("etcRentalTotalCount", etc.size());

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
    @GetMapping("/getTransactionHistory")
    public ResponseEntity<JsonObject> getTransactionHistory(@RequestParam(value="rentalIdx") Long rentalIdx,
                                                            Pageable pageable,
                                                            HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray historyArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        List<Transaction> transaction = transactionRepository.findByRental_rentalIdxAndOwnerStatusOrderByCreateAtDesc(rentalIdx, 70);
        Page<Transaction> transactions = transactionRepository.findByRental_rentalIdxAndOwnerStatusOrderByCreateAtDesc(rentalIdx, 70, pageable);

        transactions.forEach(
                tn -> {
                    JsonObject tns = new JsonObject();
                    tns.addProperty("renterSeq", tn.getUser().getUserIdx());
                    tns.addProperty("renterNickName", tn.getUser().getNickName());
                    tns.addProperty("renterImage", tn.getUser().getProfileImageUrl());
                    tns.addProperty("startDate", tn.getCreateAt().getTime());
                    tns.addProperty("completeDate", tn.getCompleteAt().getTime());
                    tns.addProperty("dailyFee", tn.getRental().getRentalPrice());

                    historyArr.add(tns);
                }
        );

        data.add("historyList", historyArr);
        data.addProperty("historyTotalCount", transaction.size());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getAlarms")
    public ResponseEntity<JsonObject> getAlarms(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray alarmArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Date currentTime = java.sql.Timestamp.valueOf(LocalDateTime.now());
        Date beforeTime = java.sql.Timestamp.valueOf(LocalDateTime.now().minusHours(720));

        List<Alarm> alarms = alarmRepository.findByHostIdxAndDeleteYnAndCreateAtGreaterThanEqualAndCreateAtLessThanEqualOrderByCreateAtDesc(findUser.getUserIdx(), false, beforeTime, currentTime);

        alarms.forEach(
                ar -> {
                    JsonObject bell = new JsonObject();
                    bell.addProperty("alarmSeq", ar.getAlarmIdx());
                    bell.addProperty("sendUserIdx", ar.getUser().getUserIdx());
                    if (ar.getUser().getProfileImageUrl() != null) {
                        bell.addProperty("sendUserProfile", ar.getUser().getProfileImageUrl());
                    } else {
                        bell.addProperty("sendUserProfile", "");
                    }
                    bell.addProperty("content", ar.getContent());
                    bell.addProperty("regDate", ar.getCreateAt().getTime());
                    bell.addProperty("readYn", ar.isReadYn());

                    if (ar.getChatIdx() != null) {
                        bell.addProperty("chatIdx", ar.getChatIdx());
                    }
                    if (ar.getRentalIdx() != null) {
                        bell.addProperty("rentalIdx", ar.getRentalIdx());
                    }
                    if (ar.getReviewIdx() != null) {
                        bell.addProperty("reviewIdx", ar.getReviewIdx());
                    }

                    alarmArr.add(bell);
                }
        );
        data.add("alarmList", alarmArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/setRental")
    public ResponseEntity<JsonObject> setRental(@RequestPart (value = "images") MultipartFile multipartFile[],
                                                @RequestParam (value = "towns") Long towns[],
                                                @RequestParam (value = "categories") Long categories[],
                                                @RequestParam (value = "title") String title,
                                                @RequestParam (value = "rentalDailyFee") int rentalDailyFee,
                                                @RequestParam (value = "content") String content,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Long leadTown = 0L;
        Long town1 = 0L;
        Long town2 = 0L;
        Long town3 = 0L;
        Long town4 = 0L;

        for (int i = 0; i < towns.length; i++) {

            switch (i) {
                case 1 :
                    town1 = towns[1];
                    break;
                case 2 :
                    town2 = towns[2];
                    break;
                case 3 :
                    town3 = towns[3];
                    break;
                case 4 :
                    town4 = towns[4];
                    break;
                default:
                    leadTown = towns[0];
                    break;
            }
        }


        Rental rental = new Rental();
        rental.setTitle(title);
        rental.setRentalPrice(rentalDailyFee);
        rental.setContent(content);
        rental.setUser(findUser);

        if (leadTown != 0L) rental.setLeadTown(leadTown);
        if (town1 != 0L) rental.setTown1(town1);
        if (town2 != 0L) rental.setTown2(town2);
        if (town3 != 0L) rental.setTown3(town3);
        if (town4 != 0L) rental.setTown4(town4);

        Rental findRental = rentalService.insertRental(rental);

        for (int i = 0; i < multipartFile.length; i++) {
            RentalImage rentalImage = new RentalImage();
            ImageFile file = uploadService.upload(multipartFile[i]);

            rentalImage.setRental(findRental);
            rentalImage.setImageUrl(file.getFileName());

            rentalImageRepository.save(rentalImage);
        }

        for (int i = 0; i < categories.length; i++) {

            RentalCategoryInfo rentalCategoryInfo = new RentalCategoryInfo();
            Category findCt = categoryRepository.getOne(categories[i]);
            rentalCategoryInfo.setCategory(findCt);
            rentalCategoryInfo.setRental(findRental);

            rentalCategoryInfoRepository.save(rentalCategoryInfo);
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
}
