package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.service.CategoryService;
import dejay.rnd.billyG.service.RentalService;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final RentalService rentalService;

    public MainController(UserService userService, UserRepository userRepository, TownService townService, TownRepository townRepository, CategoryService categoryService, RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, TownInfoRepository townInfoRepository, RentalService rentalService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.townService = townService;
        this.townRepository = townRepository;
        this.categoryService = categoryService;
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.townInfoRepository = townInfoRepository;
        this.rentalService = rentalService;
    }

    @GetMapping("/string")
    public String test() {
        return "hello";
    }

    @GetMapping("/getMainList")
    public ResponseEntity<JsonObject> getMain(@RequestParam(value="status") Integer status,
                                              @RequestParam(value="filter") Integer filter,
                                              @RequestParam(value="keyword") String keyword,
                                              @RequestParam(value="categories[]") Long[] categories,
                                              @RequestParam(value="towns[]") Long[] towns,
                                              @PageableDefault(size = 10)
                                                  Pageable pageable,
                                              HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray rentalArr = new JsonArray();
        /*Page<Rental> rentals;

        if (status == 0) {
            if (filter == 0) {
                System.out.println("MainController.getMain1");
                rentals = rentalRepository.findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndTitleContainingOrderByCreateAtDesc(categories, towns, keyword, pageable);
            } else {
                System.out.println("MainController.getMain2");
                rentals = rentalRepository.findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndTitleContainingOrderByLikeCntDesc(categories, towns, keyword, pageable);
            }
        } else {
            if (filter == 0) {
                System.out.println("MainController.getMain3");
                rentals = rentalRepository.findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndStatusAndTitleContainingOrderByCreateAtDesc(categories, towns, status, keyword, pageable);
            } else {
                System.out.println("MainController.getMain4");
                rentals = rentalRepository.findAllByRentalCategoryInfos_Category_CategoryIdxInAndRentalTownInfos_Town_TownIdxInAndStatusAndTitleContainingOrderByLikeCntDesc(categories, towns, status, keyword, pageable);
            }
        }

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
                    rentalList.addProperty("reg_date", rental.getCreateAt().getTime()/1000L);
                    rentalList.addProperty("daily_rental_fee", rental.getRentalPrice());

                    //town 리스트 추출

                    rentalArr.add(rentalList);
                }
        );
        data.add("rentals", rentalArr);*/
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getStatus")
    public ResponseEntity<JsonObject> getStatus(HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray statusArr = new JsonArray();

        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(0, "전체");
        statusMap.put(1, "렌탈가능");
        statusMap.put(2, "렌탈완료");

        for (Map.Entry<Integer, String> pair : statusMap.entrySet()) {
            JsonObject status = new JsonObject();

            status.addProperty("status_key", pair.getKey());
            status.addProperty("status", pair.getValue());

            statusArr.add(status);
        }

        data.add("status_list", statusArr);
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
                    categories.addProperty("category_seq", category.getCategoryIdx());
                    categories.addProperty("category_name", category.getName());
                    if (category.getOnImageUrl() == null) {
                        categories.addProperty("category_on_image", "");
                    } else {
                        categories.addProperty("category_on_image", category.getOnImageUrl());
                    }

                    if (category.getOffImageUrl() == null) {
                        categories.addProperty("category_off_image", "");
                    } else {
                        categories.addProperty("category_off_image", category.getOffImageUrl());
                    }

                    categoryArr.add(categories);
                }
        );
        data.add("category_list", categoryArr);

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

        /*List<Town> townList = townRepository.findByUser_userIdx(findUser.getUserIdx());

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
        );*/
        data.add("town_list", townArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

}
