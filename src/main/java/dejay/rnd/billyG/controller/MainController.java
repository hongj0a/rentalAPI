package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.MainDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.service.CategoryService;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MainController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TownService townService;
    private final TownRepository townRepository;
    private final CategoryService categoryService;
    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;
    private final TownInfoRepository townInfoRepository;

    public MainController(UserService userService, UserRepository userRepository, TownService townService, TownRepository townRepository, CategoryService categoryService, RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, TownInfoRepository townInfoRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.townService = townService;
        this.townRepository = townRepository;
        this.categoryService = categoryService;
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.townInfoRepository = townInfoRepository;
    }

    @GetMapping("/getMainList")
    public ResponseEntity<JsonObject> getMain(@RequestBody MainDto mainDto, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray categoryArr = new JsonArray();
        JsonArray townArr = new JsonArray();
        JsonArray rentalArr = new JsonArray();
        List<Rental> rentals;

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        //전체 카테고리 리스트
        List<Category> categoryList = categoryService.findAllN();

        if (mainDto.getStatus() == 2) {
            if (mainDto.getFilter() == 0) {
                rentals = rentalRepository.findAllByTitleContainingOrderByCreateAtDesc(mainDto.getKeyword());
            } else {
                rentals = rentalRepository.findAllByTitleContainingOrderByLikeCntDesc(mainDto.getKeyword());
            }
        } else {
            if (mainDto.getFilter() == 0) {
                rentals = rentalRepository.findAllByStatusAndTitleContainingOrderByCreateAtDesc(mainDto.getStatus(), mainDto.getKeyword());
            } else {
                rentals = rentalRepository.findAllByStatusAndTitleContainingOrderByLikeCntDesc(mainDto.getStatus(), mainDto.getKeyword());
            }
        }

        categoryList.forEach(
                category -> {
                    JsonObject categories = new JsonObject();
                    categories.addProperty("category_seq", category.getCategoryIdx());
                    categories.addProperty("category_name", category.getName());
                    categoryArr.add(categories);
                }
        );
        data.add("category_list", categoryArr);

        //유저별 타운 리스트
        List<Town> townList = townRepository.findByUser_userIdx(findUser.getUserIdx());

        townList.forEach(
                town -> {
                    JsonObject towns = new JsonObject();
                    towns.addProperty("town_seq", town.getTownIdx());
                    towns.addProperty("town_name", town.getTownName());
                    if(town.isLeadTown()) {
                        towns.addProperty("lead_town", "Y");
                    }
                    townArr.add(towns);
                }
        );
        data.add("town_list", townArr);

        rentals.forEach(
                rental -> {
                    JsonObject rentalList = new JsonObject();
                    rentalList.addProperty("rental_seq", rental.getRentalIdx());

                    //썸네일추출
                    List<RentalImage> rentalImages = rentalImageRepository.findByRental_rentalIdx(rental.getRentalIdx());
                    if (rentalImages.size() != 0) {
                        rentalList.addProperty("image_url", rentalImages.get(0).getImageUrl());
                    }

                    rentalList.addProperty("title", rental.getTitle());

                    //대표지역추출
                    List<RentalTownInfo> leadTown = townInfoRepository.findAllByRental_rentalIdx(rental.getRentalIdx());
                    if (leadTown.size() != 0) {
                        for (int i = 0; i < leadTown.size(); i++) {
                            if (leadTown.get(i).isLeadTown()) {
                                rentalList.addProperty("lead_town", leadTown.get(i).getTownName());
                            }
                        }
                    }

                    rentalList.addProperty("reg_date", rental.getCreateAt().getTime()/1000L);
                    rentalList.addProperty("daily_rental_fee", rental.getRentalPrice());

                    rentalArr.add(rentalList);
                }
        );
        data.add("rentals", rentalArr);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
}
