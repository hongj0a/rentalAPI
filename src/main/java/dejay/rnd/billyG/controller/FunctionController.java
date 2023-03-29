package dejay.rnd.billyG.controller;

import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.Likes;
import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.LikeDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.LikeRepository;
import dejay.rnd.billyG.repository.RentalRepository;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.LikeService;
import dejay.rnd.billyG.service.RentalService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class FunctionController {

    private final RentalRepository rentalRepository;
    private final RentalService rentalService;
    private final LikeService likeService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public FunctionController(RentalRepository rentalRepository, RentalService rentalService, LikeService likeService, UserRepository userRepository, LikeRepository likeRepository) {
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
        this.likeService = likeService;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    @PostMapping("/setLike")
    public ResponseEntity<JsonObject> setLike(@RequestBody LikeDto likeDto,
                                              HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Rental findRental = rentalRepository.getOne(likeDto.getRentalIdx());
        Likes findLike = likeRepository.findByRental_rentalIdxAndUser_userIdxAndDeleteYn(findRental.getRentalIdx(), findUser.getUserIdx(), true);

        if (likeDto.getLikeFlag() == 1) {
            //좋아요, 렌탈게시물 좋아요 수 업데이트
            if (findLike != null) {
                likeService.insertLikeInfo(findRental,findUser,findLike);
            } else {
                likeService.insertLikeInfo(findRental,findUser, null);
            }
            rentalService.updateLikeCnt(findRental, true);
            //좋아요 테이블에 row 추가

        } else {
            //좋아요취소
            findLike = likeRepository.findByRental_rentalIdxAndUser_userIdxAndDeleteYn(findRental.getRentalIdx(), findUser.getUserIdx(), false);
            rentalService.updateLikeCnt(findRental, false);
            likeService.removeLikeInfo(findLike);

        }


        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

}
