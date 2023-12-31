package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.ReportDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.repositoryImpl.UserCountRepositories;
import dejay.rnd.billyG.service.CategoryService;
import dejay.rnd.billyG.service.ReportService;
import dejay.rnd.billyG.service.UserCountService;
import dejay.rnd.billyG.service.UserMining;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final UserRepository userRepository;
    private final ReportService reportService;
    private final RentalRepository rentalRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final UserCountService userCountService;
    private final UserCountRepository userCountRepository;
    private final UserCountRepositories userCountRepositories;
    private final UserMining userMining;


    public ReportController(UserRepository userRepository, ReportService reportService, RentalRepository rentalRepository, ReviewRepository reviewRepository, CategoryService categoryService, CategoryRepository categoryRepository, UserCountService userCountService, UserCountRepository userCountRepository, UserCountRepositories userCountRepositories, UserMining userMining) {
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.rentalRepository = rentalRepository;
        this.reviewRepository = reviewRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.userCountService = userCountService;
        this.userCountRepository = userCountRepository;
        this.userCountRepositories = userCountRepositories;
        this.userMining = userMining;
    }

    //신고항목리스트
    @GetMapping("/getReportList")
    public ResponseEntity<JsonObject> getReportList (@RequestParam(name = "reportType") String reportType,
                                                 HttpServletRequest req) throws AppException {

        JsonObject data = new JsonObject();
        JsonArray reportArr = new JsonArray();

        List<Category> blockTypes = categoryService.findBlockTypes(reportType);

        blockTypes.forEach(
                blockType -> {
                    JsonObject bt = new JsonObject();
                    bt.addProperty("typeIdx", blockType.getCategoryIdx());
                    bt.addProperty("typeName", blockType.getName());

                    reportArr.add(bt);
                }
        );

        data.add("reportInfo", reportArr);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    //기타는 idx 0으로. 프론트에서 하드코딩 하기로 협의
    @PostMapping("/report")
    public ResponseEntity<JsonObject> report(@RequestBody ReportDto reportDto,
                                                        HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);
        Category findBT = categoryRepository.getOne(reportDto.getBlockTypeIdx());

        Rental findRental = new Rental();
        User reportUser = new User();
        Review findReview = new Review();

        //신고대상
        switch (reportDto.getReportFlag()) {
            case 5:
                findRental = rentalRepository.getOne(reportDto.getKeyIdx());
                break;
            case 6:
                reportUser = userRepository.getOne(reportDto.getKeyIdx());
                break;
            case 7:
                findReview = reviewRepository.getOne(reportDto.getKeyIdx());
                break;
            default:
                break;
        }

        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());
        switch (reportDto.getReportFlag()) {
                case 5:
                    reportService.insertBlockPost(findRental, findBT, reportDto.getBlockContent(), findUser.getUserIdx());

                    if (userCount != null) {
                        userCount.setBlockPostCnt(userCount.getBlockPostCnt()+1);
                        userCountService.updateCnt(userCount);

                    } else {
                        UserCount newCount = new UserCount();
                        newCount.setUser(findUser);
                        newCount.setBlockPostCnt(1L);

                        userCountRepositories.save(newCount);
                    }
                    break;
                case 6:
                    reportService.insertBlockUser(reportUser, findBT, reportDto.getBlockContent(), findUser.getUserIdx());
                    if (userCount != null) {
                        userCount.setBlockPostCnt(userCount.getBlockUserCnt()+1);
                        userCountService.updateCnt(userCount);

                    } else {
                        UserCount newCount = new UserCount();
                        newCount.setUser(findUser);
                        newCount.setBlockUserCnt(1L);

                        userCountRepositories.save(newCount);
                    }

                    break;
                case 7:
                    reportService.insertBlockReview(findReview, findBT, reportDto.getBlockContent(), findUser.getUserIdx());

                    if (userCount != null) {
                        userCount.setBlockPostCnt(userCount.getBlockReviewCnt()+1);
                        userCountService.updateCnt(userCount);
                    } else {
                        UserCount newCount = new UserCount();
                        newCount.setUser(findUser);
                        newCount.setBlockReviewCnt(1L);

                        userCountRepositories.save(newCount);
                    }
                    break;
                default:
                    break;
            }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }
}
