package dejay.rnd.billyG.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.dto.FaqDto;
import dejay.rnd.billyG.dto.FaqTypeDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.service.FaqService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/getFaqList")
    public ResponseEntity<JsonObject> getFaqList(HttpServletRequest req,
                                       @RequestParam(name = "faqType", required = false) Long faqType,
                                       @PageableDefault(size=10, sort="faqIdx", direction = Sort.Direction.DESC) Pageable pageable) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray faqArr = new JsonArray();

        /*List<FaqDto> list = faqService.findByFaqType(faqType, pageable);

        Gson gson = new Gson();
        for(FaqDto faqList: list){
            faqArr.add(gson.toJsonTree(faqList));
        }*/
        data.add("faqList",faqArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/getFaqType")
    public ResponseEntity<JsonObject> getFaqType(HttpServletRequest req,
                                                 @PageableDefault(size=10, sort="faqTypeIdx", direction = Sort.Direction.DESC)Pageable pageable) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray faqTypeArr = new JsonArray();
        //List<FaqTypeDto> list = faqService.findAll(pageable);

       /* Gson gson = new Gson();
        for(FaqTypeDto faqTypeList: list){
            faqTypeArr.add(gson.toJsonTree(faqTypeList));
        }*/

        data.add("faqType",faqTypeArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

}
