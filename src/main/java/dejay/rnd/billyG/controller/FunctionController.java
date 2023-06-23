package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.querydsl.core.Tuple;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.LikeDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.repository.*;

import dejay.rnd.billyG.repositoryImpl.RentalRepositories;
import dejay.rnd.billyG.repositoryImpl.TransactionRepositories;
import dejay.rnd.billyG.service.*;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/api")
public class FunctionController {

    private final RentalRepository rentalRepository;
    private final RentalService rentalService;
    private final LikeService likeService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final SlangsRepository slangsRepository;
    private final ToBlockRepository toBlockRepository;
    private final ToBlockService toBlockService;
    private final PushService pushService;
    private final UserCountRepository userCountRepository;
    private final UserCountService userCountService;
    private final TransactionRepositories transactionRepositories;
    private final RentalRepositories rentalRepositories;
    public FunctionController(RentalRepository rentalRepository, RentalService rentalService, LikeService likeService, UserRepository userRepository, LikeRepository likeRepository, SlangsRepository slangsRepository, ToBlockRepository toBlockRepository, ToBlockService toBlockService, PushService pushService, UserCountRepository userCountRepository, UserCountService userCountService, TransactionRepositories transactionRepositories, RentalRepositories rentalRepositories) {
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
        this.likeService = likeService;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.slangsRepository = slangsRepository;
        this.toBlockRepository = toBlockRepository;
        this.toBlockService = toBlockService;
        this.pushService = pushService;
        this.userCountRepository = userCountRepository;
        this.userCountService = userCountService;
        this.transactionRepositories = transactionRepositories;
        this.rentalRepositories = rentalRepositories;
    }

    @PostMapping("/setLike")
    public ResponseEntity<JsonObject> setLike(@RequestBody LikeDto likeDto,
                                              HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        Executor executor = Executors.newFixedThreadPool(30);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);


        Rental findRental = rentalRepository.getOne(likeDto.getRentalIdx());
        UserCount userCount = userCountRepository.findByUser_UserIdx(findRental.getUser().getUserIdx());
        Likes findLike = likeRepository.findByRental_rentalIdxAndUser_userIdx(findRental.getRentalIdx(), findUser.getUserIdx());

        if (likeDto.getLikeFlag() == 1) {
            //좋아요, 렌탈게시물 좋아요 수 업데이트
            if (findLike != null ) {
                if ( findLike.isDeleteYn() == true && findLike.getUser().getUserIdx() == findUser.getUserIdx()) {
                    likeService.updateLikeInfo(findLike, findUser);
                    data.addProperty("likeFlag", true);

                    if (findRental.getUser().isActivityNoticeYn() == true) {
                        CompletableFuture.runAsync(() -> {
                            try {
                                pushService.sendPush(new Long[]{findRental.getUser().getUserIdx()}, findUser.getUserIdx(), findRental.getRentalIdx(), 10,
                                        "새로운 좋아요", findUser.getNickName()+"님이 회원님의 "+findRental.getTitle()+" 게시물을 좋아합니다.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(Thread.currentThread().getName() + ": hi");
                        }, executor);

                    }

                } else if ( findLike.isDeleteYn() == false && findLike.getUser().getUserIdx() == findUser.getUserIdx()){
                    apiRes.setStatus(9999);
                    apiRes.setMessage("이미 좋아요 한 게시물");
                    return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
                }

            } else {
                if (findRental.getUser().isActivityNoticeYn() == true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            pushService.sendPush(new Long[]{findRental.getUser().getUserIdx()}, findUser.getUserIdx(), findRental.getRentalIdx(), 10,
                                    "새로운 좋아요", findUser.getNickName()+"님이 회원님의 "+findRental.getTitle()+" 게시물을 좋아합니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);
                }

                likeService.insertLikeInfo(findRental,findUser);
            }
            rentalService.updateLikeCnt(findRental, true);
            //좋아요 테이블에 row 추가
            if (userCount != null) {
                userCount.setAllLikeCnt(userCount.getAllLikeCnt()+1);
                userCountService.updateCnt(userCount);

            } else {
                UserCount newCount = new UserCount();
                newCount.setUser(findUser);
                newCount.setAllLikeCnt(1L);

                userCountRepository.save(newCount);
            }

        } else {
            //좋아요취소
            if (findLike != null && findLike.isDeleteYn() == false) {
                likeService.removeLikeInfo(findLike);
                rentalService.updateLikeCnt(findRental, false);
                data.addProperty("likeFlag", false);
            } else {
                apiRes.setError(ErrCode.err_api_is_not_exist_like.code());
                apiRes.setMessage(ErrCode.err_api_is_not_exist_like.msg());
                return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
            }

            if (userCount != null) {
                userCount.setAllLikeCnt(userCount.getAllLikeCnt()-1);
                userCountService.updateCnt(userCount);
            }

        }



        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getSlangs")
    public ResponseEntity<JsonObject> getSlangs(HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray slangArr = new JsonArray();

        List<Slangs> list = slangsRepository.findAllByActiveYn(true);

        list.forEach(
                sl -> {
                    slangArr.add(sl.getSlang());
                }
        );

        data.add("slangs", slangArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    //좋아요처럼 변경
    @PostMapping("/setBlockControl")
    public ResponseEntity<JsonObject> setBlockControl(HttpServletRequest req,
                                                      @RequestBody UserDto userDto) throws ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        ToBlock findOne;

        if (userDto.isBlockYn() == true) {
            //차단을 하겠다는 의미
            //일단 차단을 했던 이력이 있는지 조회
            //deleteyn true
            findOne = toBlockRepository.findByUser_userIdxAndBlockUser_userIdxAndDeleteYn(findUser.getUserIdx(), userDto.getUserIdx(), true);

            if (findOne != null) {
                findOne.setDeleteYn(false);
                toBlockService.setBlockInfoUpdate(findOne);
            } else {
                ToBlock toBlock = new ToBlock();
                User findBlockUser = userRepository.getOne(userDto.getUserIdx());

                toBlock.setBlockUser(findBlockUser);
                toBlock.setUser(findUser);
                toBlockService.insertBlockInfo(toBlock);
            }
        } else {
            //차단을 풀겠다는 의미
            //차단했던 이력 조회
            //무조건 있어야함
            findOne = toBlockRepository.findByUser_userIdxAndBlockUser_userIdxAndDeleteYn(findUser.getUserIdx(), userDto.getUserIdx(), false);
            if (findOne != null) {
                findOne.setDeleteYn(true);
                toBlockService.setBlockInfoUpdate(findOne);
            }
        }


        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }


}
