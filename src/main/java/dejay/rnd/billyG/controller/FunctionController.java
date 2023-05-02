package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.LikeDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.repository.LikeRepository;
import dejay.rnd.billyG.repository.RentalRepository;
import dejay.rnd.billyG.repository.SlangsRepository;
import dejay.rnd.billyG.repository.UserRepository;

import dejay.rnd.billyG.service.LikeService;
import dejay.rnd.billyG.service.RentalService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class FunctionController {

    private final RentalRepository rentalRepository;
    private final RentalService rentalService;
    private final LikeService likeService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final SlangsRepository slangsRepository;

    public FunctionController(RentalRepository rentalRepository, RentalService rentalService, LikeService likeService, UserRepository userRepository, LikeRepository likeRepository, SlangsRepository slangsRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
        this.likeService = likeService;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.slangsRepository = slangsRepository;
    }

    @PostMapping("/setLike")
    public ResponseEntity<JsonObject> setLike(@RequestBody LikeDto likeDto,
                                              HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(likeDto.getRentalIdx());
        Likes findLike = likeRepository.findByRental_rentalIdxAndUser_userIdx(findRental.getRentalIdx(), findUser.getUserIdx());

        if (likeDto.getLikeFlag() == 1) {
            //좋아요, 렌탈게시물 좋아요 수 업데이트
            if (findLike != null ) {
                if ( findLike.isDeleteYn() == true && findLike.getUser().getUserIdx() == findUser.getUserIdx()) {
                    likeService.updateLikeInfo(findLike, findUser);
                    data.addProperty("likeFlag", true);
                    //likeService.insertLikeInfo(findRental,findUser);
                } else if ( findLike.isDeleteYn() == false && findLike.getUser().getUserIdx() == findUser.getUserIdx()){
                    apiRes.setStatus(9999);
                    apiRes.setMessage("이미 좋아요 한 게시물");
                    return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
                }

            } else {
                likeService.insertLikeInfo(findRental,findUser);
            }
            rentalService.updateLikeCnt(findRental, true);
            //좋아요 테이블에 row 추가

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
}
