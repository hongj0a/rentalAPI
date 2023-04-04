package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.ReportDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.service.CategoryService;
import dejay.rnd.billyG.service.ReportService;
import dejay.rnd.billyG.util.UserMiningUtil;
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


    public ReportController(UserRepository userRepository, ReportService reportService, RentalRepository rentalRepository, ReviewRepository reviewRepository, CategoryService categoryService, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.rentalRepository = rentalRepository;
        this.reviewRepository = reviewRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/getReportList")
    public ResponseEntity<JsonObject> getReportList (@RequestParam(name = "reportType") String reportType,
                                                 HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray reportArr = new JsonArray();

        List<Category> blockTypes = categoryService.findBlockTypes(reportType);
        //List<BlockType> blockTypes = blockTypeRepository.findByTypeFlagAndActiveYnAndDeleteYn(reportType, true, false);

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

    @PostMapping("/report")
    public ResponseEntity<JsonObject> report(@RequestBody ReportDto reportDto,
                                                        HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);
        Category findBT = categoryRepository.getOne(reportDto.getBlockTypeIdx());

        Rental findRental = new Rental();
        User reportUser = new User();
        Review findReview = new Review();

        //신고대상
        if (reportDto.getRentalIdx() != null) {
            findRental = rentalRepository.getOne(reportDto.getRentalIdx());
        } else if (reportDto.getUserIdx() != null) {
            reportUser = userRepository.getOne(reportDto.getUserIdx());
        } else if (reportDto.getReviewIdx() != null){
            findReview = reviewRepository.getOne(reportDto.getReviewIdx());
        }

        switch (reportDto.getReportFlag()) {
                case 5:
                    reportService.insertBlockPost(findRental, findBT, reportDto.getBlockContent(), findUser.getUserIdx());
                    break;
                case 6:
                    reportService.insertBlockUser(reportUser, findBT, reportDto.getBlockContent(), findUser.getUserIdx());
                    break;
                default:
                    reportService.insertBlockReview(findReview, findBT, reportDto.getBlockContent(), findUser.getUserIdx());
                    break;
            }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }
}
