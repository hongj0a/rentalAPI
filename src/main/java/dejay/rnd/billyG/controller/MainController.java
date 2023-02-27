package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.Category;
import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.MainDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.RentalRepository;
import dejay.rnd.billyG.repository.TownRepository;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.service.CategoryService;
import dejay.rnd.billyG.service.TownService;
import dejay.rnd.billyG.service.UserService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Sort;
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

    public MainController(UserService userService, UserRepository userRepository, TownService townService, TownRepository townRepository, CategoryService categoryService, RentalRepository rentalRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.townService = townService;
        this.townRepository = townRepository;
        this.categoryService = categoryService;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/getMainList")
    public ResponseEntity<JsonObject> getMain(@RequestBody MainDto mainDto, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray categoryArr = new JsonArray();
        JsonArray townArr = new JsonArray();
        List<Rental> rentals;

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        //전체 카테고리 리스트
        List<Category> categoryList = categoryService.findAllN();

        if ( null == mainDto.getStatus() ) {
            System.out.println("MainController.getMain");
            mainDto.setStatus(2);
            rentals = rentalRepository.findAllByStatusNotAndTitleContainingOrderByLikeCntDesc(mainDto.getStatus(), mainDto.getKeyword());
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

        System.out.println("mainDto = " + mainDto.getKeyword());

        if (mainDto.getFilter() == 0) {
            rentals = rentalRepository.findAllByStatusAndTitleContainingOrderByCreateAtDesc(mainDto.getStatus(), mainDto.getKeyword());
        } else {
            rentals = rentalRepository.findAllByStatusAndTitleContainingOrderByLikeCntDesc(mainDto.getStatus(), mainDto.getKeyword());
        }

        System.out.println("rentals = " + rentals.size());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }
}
