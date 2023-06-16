package dejay.rnd.billyG.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.config.ImageProperties;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.MainDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repositoryImpl.RentalRepositories;
import dejay.rnd.billyG.repositoryImpl.TownRepositories;
import dejay.rnd.billyG.repositoryImpl.UserCountRepositories;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import dejay.rnd.billyG.service.*;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private final GradeRepository gradeRepository;
    private final FileUploadService uploadService;
    private final CategoryRepository categoryRepository;
    private final UserCountRepository userCountRepository;
    private final UserCountRepositories userCountRepositories;
    private final BellScheduleRepositry bellScheduleRepository;
    private final BellScheduleService bellScheduleService;
    private final BlockUserRepository blockUserRepository;
    private final BlockPostRepository blockPostRepository;
    private final Path fileStorageLocation;
    private final ToBlockRepository toBlockRepository;
    private final UserRepositories userRepositories;
    private final PushService pushService;
    private final UserCountService userCountService;

    public MainController(ImageProperties imageProperties, UserRepository userRepository, TownRepository townRepository, TownRepositories townRepositories, CategoryService categoryService, RentalRepository rentalRepository, RentalRepositories rentalRepositories, RentalImageRepository rentalImageRepository, RentalCategoryInfoRepository rentalCategoryInfoRepository, RentalService rentalService, TransactionRepository transactionRepository, LikeRepository likeRepository, AlarmRepository alarmRepository, ReviewRepository reviewRepository, GradeRepository gradeRepository, FileUploadService uploadService, CategoryRepository categoryRepository, UserCountRepository userCountRepository, UserCountRepositories userCountRepositories, BellScheduleRepositry bellScheduleRepository, BellScheduleService bellScheduleService, BlockUserRepository blockUserRepository, BlockPostRepository blockPostRepository, ToBlockRepository toBlockRepository, UserRepositories userRepositories, PushService pushService, UserCountService userCountService) {
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
        this.gradeRepository = gradeRepository;
        this.uploadService = uploadService;
        this.categoryRepository = categoryRepository;
        this.userCountRepository = userCountRepository;
        this.userCountRepositories = userCountRepositories;
        this.fileStorageLocation = Paths.get(imageProperties.getDefaultPath())
                .toAbsolutePath().normalize();
        this.bellScheduleRepository = bellScheduleRepository;
        this.bellScheduleService = bellScheduleService;
        this.blockUserRepository = blockUserRepository;
        this.blockPostRepository = blockPostRepository;
        this.toBlockRepository = toBlockRepository;
        this.userRepositories = userRepositories;
        this.pushService = pushService;
        this.userCountService = userCountService;
    }

    @GetMapping("/getMainList")
    public ResponseEntity<JsonObject> getMain(@RequestParam(value="status") Integer status,
                                              @RequestParam(required = false, value="filter") Integer filter,
                                              @RequestParam(required = false, value="keyword") String keyword,
                                              @RequestParam(required = false, value="categories") Long[] categories,
                                              @RequestParam(required = false, value="towns") Long[] towns, Pageable pageable,
                                              HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray rentalArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        /**
         * [렌탈 상태]
         * 렌탈가능 [Enable(1)]
         * 렌탈중 [ing(2)]
         * 렌탈완료 [complete(3)]
         * 렌탈숨기기 [hide(4)]
         * =================================
         * 파라미터는 렌탈가능(1) or 렌탈중(2)
         * if status 전체(0) == 1,2
         * else if status 렌탈가능(1) == 1
         * else status 렌탈완료(2) == 2
         */

        ArrayList<Integer> p_status = new ArrayList<>();

        if (status == 0) {
            p_status.add(1);
            p_status.add(2);
        } else if (status == 1) {
            p_status.add(1);
        } else {
            p_status.add(2);
        }

        List<Rental> totalCount = rentalRepositories.getTotalCount(p_status, keyword, towns, categories);

        List<ToBlock> findBlock = toBlockRepository.findByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);

        ArrayList<Long> blocks = new ArrayList<>();
        for (int i = 0; i < findBlock.size(); i++) {
            blocks.add(findBlock.get(i).getBlockUser().getUserIdx());
        }

        ArrayList<Long> sizes = new ArrayList<>();
        for (int j = 0; j < totalCount.size(); j++) {
            if (!blocks.contains(totalCount.get(j).getUser().getUserIdx())) {
                sizes.add(totalCount.get(j).getRentalIdx());
            }
        }

        Page<Rental> mains = rentalRepositories.findAll(p_status, filter, keyword, towns, categories, pageable);
        mains.forEach(
                rental -> {

                    if (!blocks.contains(rental.getUser().getUserIdx())) {
                        JsonObject rentalList = new JsonObject();

                        rentalList.addProperty("rentalSeq", rental.getRentalIdx());

                        //썸네일추출
                        List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(rental.getRentalIdx());
                        if (rentalImages.size() != 0) {
                            rentalList.addProperty("imageUrl", rentalImages.get(0).getImageUrl());
                        }

                        rentalList.addProperty("title", rental.getTitle());
                        rentalList.addProperty("content", rental.getContent());
                        rentalList.addProperty("regDate", rental.getPullUpAt().getTime());
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

                }
        );
        data.add("rentals", rentalArr);
        data.addProperty("totalCount", sizes.size());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getStatus")
    public ResponseEntity<JsonObject> getStatus(HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray statusArr = new JsonArray();

        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(1, "렌탈가능");
        statusMap.put(2, "렌탈진행중");

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

    //수정 조회에서도 같이 씀
    @Transactional(rollbackFor = Exception.class)
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

        Grade grade = gradeRepository.getOne(findUser.getUserLevel());
        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());

        Rental findRental = rentalRepository.getOne(rentalIdx);

        BlockUser blockUser = blockUserRepository.findByReporterIdxAndUser_userIdxAndProcessingStatusNotIn(findUser.getUserIdx(), findRental.getUser().getUserIdx(),  new int[]{2});
        if (blockUser != null) {
            data.addProperty("blockUserHistory", true);
        } else {
            data.addProperty("blockUserHistory", false);
        }

        BlockPost blockPost = blockPostRepository.findByReporterIdxAndRental_rentalIdxAndProcessingStatusNotIn(findUser.getUserIdx(), findRental.getRentalIdx(), new int[]{2});
        if (blockPost != null) {
            data.addProperty("blockPostHistory", true);
        } else {
            data.addProperty("blockPostHistory", false);
        }

        List<Review> reviews = reviewRepository.findByOwnerIdxAndActiveYnAndDeleteYnOrderByCreateAtDesc(findRental.getUser().getUserIdx(), true, false);

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

        BellSchedule findBell = bellScheduleRepository.findByUser_userIdxAndRental_rentalIdxAndDeleteYn(findUser.getUserIdx(), findRental.getRentalIdx(), false);

        if (findBell == null || findBell.isDeleteYn() == true) {
            data.addProperty("bellButton", true);
        } else if (findBell != null && findBell.isDeleteYn() == false) {
            data.addProperty("bellButton", false);
        }
        data.addProperty("userNickName", findRental.getUser().getNickName());
        data.addProperty("userIdx", findRental.getUser().getUserIdx());
        data.addProperty("userStarPoint", Float.parseFloat(findRental.getUser().getStarPoint()));
        data.addProperty("activityScore", findRental.getUser().getActivityScore());
        data.addProperty("grade", grade.getGradeName());
        data.addProperty("maxScore", getGrade.getGradeScore());
        data.addProperty("reviewCount", reviews.size());

        if (findRental.getUser().getUserIdx() != findUser.getUserIdx()) {
            //조회수 업데이트
            rentalService.updateViewCnt(findRental);

            if (userCount != null) {
                userCount.setViewCnt(userCount.getViewCnt()+1);
                userCountService.updateCnt(userCount);

            } else {
                UserCount newCount = new UserCount();
                newCount.setUser(findUser);
                newCount.setViewCnt(1L);

                userCountRepositories.save(newCount);
            }

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


        data.addProperty("rentalSeq", findRental.getRentalIdx());
        data.addProperty("rentalStatus", findRental.getStatus());
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

        Page<Rental> etcRentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotInOrderByCreateAtDesc(findRental.getUser().getUserIdx(), true, false, new int[]{4}, pageable);
        List<Rental> etc = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusNotIn(findRental.getUser().getUserIdx(), true, false, new int[]{4});

        etcRentals.forEach(
                etcs -> {
                    JsonObject etcRental = new JsonObject();

                    List<RentalImage> renImgs = rentalImageRepository.findByRental_rentalIdx(etcs.getRentalIdx());

                    etcRental.addProperty("rentalSeq", etcs.getRentalIdx());
                    etcRental.addProperty("imageUrl", renImgs.get(0).getImageUrl());
                    etcRental.addProperty("title", etcs.getTitle());
                    etcRental.addProperty("dailyRentalFee", etcs.getRentalPrice());

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
                    tns.addProperty("renterIdx", tn.getUser().getUserIdx());
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
        Date beforeTime = java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(30));

        List<Alarm> alarms = alarmRepository.findByHostIdxAndCreateAtGreaterThanEqualAndCreateAtLessThanEqualOrderByCreateAtDesc(findUser.getUserIdx(),  beforeTime, currentTime);

        alarms.forEach(
                ar -> {
                    JsonObject bell = new JsonObject();
                    bell.addProperty("alarmSeq", ar.getAlarmIdx());
                    if (ar.getUser() != null) {
                        bell.addProperty("sendUserIdx", ar.getUser().getUserIdx());
                        if (ar.getUser().getNickName() != null) {
                            bell.addProperty("sendUserNickName", ar.getUser().getNickName());
                        } else {
                            bell.addProperty("sendUserNickName", "");
                        }
                        if (ar.getUser().getProfileImageUrl() != null) {
                            bell.addProperty("sendUserProfile", ar.getUser().getProfileImageUrl());
                        } else {
                            bell.addProperty("sendUserProfile", "");
                        }
                    }
                    if (ar.getAdmin() != null) {
                        bell.addProperty("sendUserIdx", 0);
                        bell.addProperty("sendUserProfile", "");
                    }
                    bell.addProperty("content", ar.getContent());
                    bell.addProperty("regDate", ar.getCreateAt().getTime());
                    bell.addProperty("readYn", ar.isReadYn());
                    bell.addProperty("targetIdx", ar.getTargetIdx());
                    bell.addProperty("type", ar.getType());

                    alarmArr.add(bell);
                }
        );
        data.add("alarmList", alarmArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }



    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/setRental", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> setRental(@RequestParam (value = "images") List<MultipartFile> images,
                                                @RequestParam (value = "title") String title,
                                                @RequestParam (value = "content") String content,
                                                @RequestParam (value = "categories") String[] categories,
                                                @RequestParam (value = "towns") String[] towns,
                                                @RequestParam (value ="rentalDailyFee") String rentalDailyFee,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        Executor executor = Executors.newFixedThreadPool(30);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Long[] newTwns = new Long[towns.length];
        for (int i = 0; i < towns.length; i++) {
            newTwns[i] = Long.valueOf(towns[i]);
        }
        Long leadTown = 0L;
        Long town1 = 0L;
        Long town2 = 0L;
        Long town3 = 0L;
        Long town4 = 0L;

        for (int i = 0; i < towns.length; i++) {

            switch (i) {
                case 1 :
                    town1 = Long.valueOf(towns[1]);
                    break;
                case 2 :
                    town2 = Long.valueOf(towns[2]);
                    break;
                case 3 :
                    town3 = Long.valueOf(towns[3]);
                    break;
                case 4 :
                    town4 = Long.valueOf(towns[4]);
                    break;
                default:
                    leadTown = Long.valueOf(towns[0]);
                    break;
            }
        }


        Rental rental = new Rental();
        rental.setTitle(title);
        rental.setRentalPrice(Long.valueOf(rentalDailyFee));
        rental.setContent(content);
        rental.setUser(findUser);
        rental.setStatus(1);

        if (leadTown != 0L) rental.setLeadTown(leadTown);
        if (town1 != 0L) rental.setTown1(town1);
        if (town2 != 0L) rental.setTown2(town2);
        if (town3 != 0L) rental.setTown3(town3);
        if (town4 != 0L) rental.setTown4(town4);

        Rental findRental = rentalService.insertRental(rental);

        for (int i = 0; i < images.size(); i++) {
            RentalImage rentalImage = new RentalImage();
            ImageFile file = uploadService.upload(images.get(i));

            rentalImage.setRental(findRental);
            rentalImage.setImageUrl(file.getFileName());

            rentalImageRepository.save(rentalImage);
        }

        for (int i = 0; i < categories.length; i++) {

            RentalCategoryInfo rentalCategoryInfo = new RentalCategoryInfo();
            Category findCt = categoryRepository.getOne(Long.valueOf(categories[i]));
            rentalCategoryInfo.setCategory(findCt);
            rentalCategoryInfo.setRental(findRental);

            rentalCategoryInfoRepository.save(rentalCategoryInfo);
        }

        List<User> users = userRepositories.findUsers(newTwns);
        if (users.size() != 0) {
            Long[] hosts = new Long[users.size()];

            for (int i = 0; i < hosts.length; i++) {
                hosts[i] = users.get(i).getUserIdx();
            }


            CompletableFuture.runAsync(() -> {
                try {
                    pushService.sendPush(hosts, findUser.getUserIdx(), findRental.getRentalIdx(),
                            10, "[알림] 렌탈가능 물품", "회원님의 동네로 등록된 새게시글이 있습니다.");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ": hi");
            }, executor);
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    //편집하기 전에 게시글, 마이 타운정보 리스트 get api 있어야 함
    @GetMapping("/getDetailTowns")
    public ResponseEntity<JsonObject> getDetailTowns(@RequestParam (value = "rentalIdx") Long rentalIdx,
                                                     HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(rentalIdx);

        ArrayList<Long> sumTownInfo = new ArrayList<>();
        ArrayList<Long> orgTownInfo = new ArrayList<>();


        sumTownInfo.add(findUser.getLeadTown());
        if (findUser.getTown1() != null ) sumTownInfo.add(findUser.getTown1());
        if (findUser.getTown2() != null ) sumTownInfo.add(findUser.getTown2());
        if (findUser.getTown3() != null ) sumTownInfo.add(findUser.getTown3());
        if (findUser.getTown4() != null ) sumTownInfo.add(findUser.getTown4());

        ArrayList<Long> myTownInfo = (ArrayList<Long>) sumTownInfo.clone();

        sumTownInfo.add(findRental.getLeadTown());
        orgTownInfo.add(findRental.getLeadTown());

        if (findRental.getTown1() != null ) {
            sumTownInfo.add(findRental.getTown1());
            orgTownInfo.add(findRental.getTown1());
        }
        if (findRental.getTown2() != null ) {
            sumTownInfo.add(findRental.getTown2());
            orgTownInfo.add(findRental.getTown2());

        }
        if (findRental.getTown3() != null ) {
            sumTownInfo.add(findRental.getTown3());
            orgTownInfo.add(findRental.getTown3());
        }
        if (findRental.getTown4() != null ) {
            sumTownInfo.add(findRental.getTown4());
            orgTownInfo.add(findRental.getTown4());
        }

        Set<Long> setTowns = new HashSet<Long>(sumTownInfo);
        List<Long> newTowns = new ArrayList<Long>(setTowns);

        List<Town> names = townRepository.findAllById(newTowns);

        names.forEach(
                name -> {
                    JsonObject nameObj = new JsonObject();

                    //기존 게시글에 등록된 타운리스트에 포함되면
                    if (orgTownInfo.contains(name.getTownIdx())) {
                        //게시글에 포함된 타운체크
                        nameObj.addProperty("isChecked", true);
                        if(myTownInfo.contains(name.getTownIdx())) {
                            //그 중에 마이페이지 타운리스트랑 겹치면 편집가능 / editable
                            nameObj.addProperty("isEditable", true);
                        } else {
                            nameObj.addProperty("isEditable", false);
                        }
                    } else {
                        nameObj.addProperty("checked", false);
                    }
                    nameObj.addProperty("townSeq", name.getTownIdx());
                    nameObj.addProperty("townName", name.getTownName());

                    townArr.add(nameObj);
                }
        );
        data.add("allTowns", townArr);
        
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    //카테고리 정보는 보류
    @GetMapping("/getDetailCategories")
    public ResponseEntity<JsonObject> getDetailCategories(@RequestParam (value = "rentalIdx") Long rentalIdx,
                                                     HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray townArr = new JsonArray();

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(rentalIdx);
        List<RentalCategoryInfo> findCates = rentalCategoryInfoRepository.findByRental_rentalIdx(rentalIdx);

        findCates.forEach(
                name -> {
                    JsonObject nameObj = new JsonObject();

                    nameObj.addProperty("townSeq", name.getCategory().getCategoryIdx());
                    nameObj.addProperty("townName", name.getCategory().getName());

                    townArr.add(nameObj);
                }
        );
        data.add("checkedCategoryList", townArr);

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/editRental", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> editRental(@RequestParam (value = "images") List<MultipartFile> images,
                                                 @RequestParam (value = "title") String title,
                                                 @RequestParam (value = "content") String content,
                                                 @RequestParam (value = "categories") String[] categories,
                                                 @RequestParam (value = "towns") String[] towns,
                                                 @RequestParam (value ="rentalDailyFee") String rentalDailyFee,
                                                 @RequestParam (value = "rentalIdx") String rentalIdx,
                                                 HttpServletRequest req) throws AppException, ParseException, IOException {
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

        /**
         * 원래의 게시글을 찾아서
         * 배열의 길이는 몇개가 올지 모르니깐
         * 기존에 있던 디비 파일이름들 다 삭제
         * 1. 이미지 이름만 있는 애들을 새로운 배열에 저장, 디비에 저장
         * 2. 파일이 있는애들은 업로드하고, 디비에 저장
         * 1,2번 2중포문이 돌면서 두 배열의 다른 값을 서버에서 이미지 삭제
         * 타운 전체 삭제 후 받은 정보로 다시 저장
         * 카테고리 전체 삭제 후 받은 정보로 다시 저장
         * title,content,status update
         */

        Rental orgRental = rentalRepository.getOne(Long.valueOf(rentalIdx));

        List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(Long.valueOf(rentalIdx));
        ArrayList<String> existFiles = new ArrayList<>();

        if (rentalImages.size() != 0) {
            for (int i = 0; i < rentalImages.size(); i++) {
                rentalImageRepository.delete(rentalImages.get(i));
            }
        }

        //디비에 있는 이미지 이름들은 다 삭제
        for (MultipartFile item: images)
        {
            RentalImage rentalImage = new RentalImage();

            if (item.isEmpty())
            {
                //이미지 이름만 있는애들
                rentalImage.setRental(orgRental);
                rentalImage.setImageUrl(item.getOriginalFilename());
                existFiles.add(item.getOriginalFilename());
            }
            else
            {
                //파일이 있는애들
                ImageFile file = uploadService.upload(item);
                rentalImage.setImageUrl(file.getFileName());
                rentalImage.setRental(orgRental);

            }
            //이미지 이름들은 다 저장이되고
            rentalImageRepository.save(rentalImage);
        }

        for (int i = 0; i < rentalImages.size(); i++) {
            boolean toDelete = true;
            for (int j = 0; j < existFiles.size(); j++) {
                if (rentalImages.get(i).getImageUrl().equals(existFiles.get(j))) {
                    toDelete = false;
                }
            }
            if (toDelete) {
                File file = new File(fileStorageLocation + File.separator + rentalImages.get(i));
                if (file.exists()) {
                    file.delete();
                }
            }
        }


        List<RentalCategoryInfo> categoryInfos = rentalCategoryInfoRepository.findByRental_rentalIdx(Long.valueOf(rentalIdx));
        for (int i = 0; i < categoryInfos.size(); i++) {
            rentalCategoryInfoRepository.delete(categoryInfos.get(i));
        }

        if (orgRental.getLeadTown()!= null) orgRental.setLeadTown(0L);
        if (orgRental.getTown1()!= null) orgRental.setTown1(0L);
        if (orgRental.getTown2()!= null) orgRental.setTown2(0L);
        if (orgRental.getTown3()!= null) orgRental.setTown3(0L);
        if (orgRental.getTown4()!= null) orgRental.setTown4(0L);

        for (int i = 0; i < towns.length; i++) {

            switch (i) {
                case 1 :
                    town1 = Long.valueOf(towns[1]);
                    break;
                case 2 :
                    town2 = Long.valueOf(towns[2]);
                    break;
                case 3 :
                    town3 = Long.valueOf(towns[3]);
                    break;
                case 4 :
                    town4 = Long.valueOf(towns[4]);
                    break;
                default:
                    leadTown = Long.valueOf(towns[0]);
                    break;
            }
        }


        orgRental.setTitle(title);
        orgRental.setRentalPrice(Long.valueOf(rentalDailyFee));
        orgRental.setContent(content);
        orgRental.setUpdator(findUser.getEmail());

        if (leadTown != 0L) orgRental.setLeadTown(leadTown);
        if (town1 != 0L) orgRental.setTown1(town1);
        if (town2 != 0L) orgRental.setTown2(town2);
        if (town3 != 0L) orgRental.setTown3(town3);
        if (town4 != 0L) orgRental.setTown4(town4);

        rentalService.updateRental(orgRental);


        for (int i = 0; i < categories.length; i++) {

            RentalCategoryInfo rentalCategoryInfo = new RentalCategoryInfo();
            Category findCt = categoryRepository.getOne(Long.valueOf(categories[i]));
            rentalCategoryInfo.setCategory(findCt);
            rentalCategoryInfo.setRental(orgRental);

            rentalCategoryInfoRepository.save(rentalCategoryInfo);
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    //공개게시글 (status in 1,3) -> 4로 변경 가능
    //숨김게시글 (status == 4) -> 1로 변경 가능
    //공개,숨김 상태변경 + 끌어올리기
    @PostMapping("/editStatus")
    public ResponseEntity<JsonObject> editStatus(HttpServletRequest req,
                                                 @RequestBody MainDto mainDto) throws ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(mainDto.getRentalIdx());

        if (mainDto.getStatus() == 99) {
            findRental.setPullUpCnt(findRental.getPullUpCnt()+1);
            findRental.setUpdator(findUser.getEmail());
            rentalService.pullUpRental(findRental);
        } else {
            findRental.setStatus(mainDto.getStatus());
            findRental.setUpdator(findUser.getEmail());
            rentalService.updateRental(findRental);
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }


    //삭제
    @PostMapping("/deleteRental")
    public ResponseEntity<JsonObject> deleteRental(HttpServletRequest req,
                                                 @RequestBody MainDto mainDto) throws ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(mainDto.getRentalIdx());

        findRental.setDeleteYn(true);
        findRental.setUpdator(findUser.getEmail());
        rentalService.deleteRental(findRental);

        List<RentalImage> imgs = rentalImageRepository.findByRental_rentalIdx(findRental.getRentalIdx());

        for (int i = 0; i < imgs.size(); i++) {

            File file = new File(fileStorageLocation + File.separator + imgs.get(i).getImageUrl());

            if (file.exists()) {
                file.delete();
            }

            rentalImageRepository.delete(imgs.get(i));
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    //내 게시글 (공개게시글/숨김게시글) 조회
    @GetMapping("/getMyRental")
    public ResponseEntity<JsonObject> getMyRental(@RequestParam(value="status") int status, Pageable pageable,
                                                    HttpServletRequest req) throws AppException, ParseException, java.text.ParseException {
        JsonObject data = new JsonObject();
        JsonArray renArr = new JsonArray();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        ArrayList<Integer> p_status = new ArrayList<>();

        if (status != 4) {
            p_status.add(1);
            p_status.add(2);
        } else {
            p_status.add(4);
        }

        Page<Rental> myRentals = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusInOrderByCreateAtDesc(findUser.getUserIdx(), true, false, p_status, pageable);
        List<Rental> my = rentalRepository.findByUser_userIdxAndActiveYnAndDeleteYnAndStatusIn(findUser.getUserIdx(), true, false, p_status);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date1 = dateFormat.parse(dateFormat.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, -7);
        myRentals.forEach(
                etcs -> {
                    JsonObject etcRental = new JsonObject();

                    List<RentalImage> renImgs = rentalImageRepository.findByRental_rentalIdx(etcs.getRentalIdx());
                    etcRental.addProperty("rentalSeq", etcs.getRentalIdx());
                    etcRental.addProperty("imageUrl", renImgs.get(0).getImageUrl());
                    etcRental.addProperty("title", etcs.getTitle());
                    etcRental.addProperty("dailyFee", etcs.getRentalPrice());
                    etcRental.addProperty("regDate", etcs.getPullUpAt().getTime());
                    Date date2 = etcs.getPullUpAt();
                    if (date2.before(date1)) {
                        etcRental.addProperty("pullUpYn", true);
                    } else {
                        etcRental.addProperty("pullUpYn", false);
                    }


                    switch (etcs.getStatus()) {
                        case 2 :
                            etcRental.addProperty("status", "렌탈진행중");
                            etcRental.addProperty("editable", false);
                            break;
                        case 4:
                            etcRental.addProperty("status", "렌탈숨기기");
                            etcRental.addProperty("editable", true);
                            break;
                        default:
                            etcRental.addProperty("status", "렌탈가능");
                            etcRental.addProperty("editable", true);
                            break;
                    }

                    //town 리스트 추출
                    List<String> tLst = new ArrayList<>();

                    if (null != etcs.getLeadTown()) {
                        Town Ltown = townRepository.getOne(etcs.getLeadTown());
                        tLst.add(Ltown.getTownName());
                    }

                    if ( null != etcs.getTown1() ) {
                        Town town1 = townRepository.getOne(etcs.getTown1());
                        tLst.add(town1.getTownName());

                    }

                    if ( null != etcs.getTown2() ) {
                        Town town2 = townRepository.getOne(etcs.getTown2());
                        tLst.add(town2.getTownName());
                    }

                    if ( null != etcs.getTown3() ) {
                        Town town3 = townRepository.getOne(etcs.getTown3());
                        tLst.add(town3.getTownName());
                    }

                    if ( null != etcs.getTown4() ) {
                        Town town4 = townRepository.getOne(etcs.getTown4());
                        tLst.add(town4.getTownName());
                    }

                    Gson gson = new Gson();
                    etcRental.add("towns", gson.toJsonTree(tLst));

                    renArr.add(etcRental);
                }
        );

        data.add("myRentals", renArr);
        data.addProperty("etcRentalTotalCount", my.size());

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping(value = "/setSchedule")
    public ResponseEntity<JsonObject> setSchedule(@RequestBody MainDto mainDto,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        
        User findUser = userRepository.findByEmail(userEmail);
        Rental findRental = rentalRepository.getOne(mainDto.getRentalIdx());

        BellSchedule findBell = bellScheduleRepository.findByUser_userIdxAndRental_rentalIdxAndDeleteYn(findUser.getUserIdx(), findRental.getRentalIdx(),false);

        if (findBell == null) {
            BellSchedule bellSchedule = new BellSchedule();
            bellSchedule.setRental(findRental);
            bellSchedule.setUser(findUser);
            bellSchedule.setDeleteYn(false);

            bellScheduleRepository.save(bellSchedule);
        } else if (findBell != null && findBell.isDeleteYn() == false ) {
            findBell.setDeleteYn(true);
            bellScheduleService.update(findBell);
        } else if (findBell != null && findBell.isDeleteYn() == true) {
            findBell.setDeleteYn(false);
            bellScheduleService.update(findBell);
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

}
