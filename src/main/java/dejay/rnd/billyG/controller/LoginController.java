package dejay.rnd.billyG.controller;

import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/signup")
    public ResponseEntity<JsonObject> getRentalList (@Valid @RequestBody User user,
                                                     HttpServletRequest req) throws AppException {

        JsonObject data = new JsonObject();


        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

}
