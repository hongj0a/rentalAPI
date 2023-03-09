package dejay.rnd.billyG.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.dto.NoticeDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.service.NoticeService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/getNotice")
    public ResponseEntity<JsonObject> getNoticeList(HttpServletRequest req, @PageableDefault(size=10, sort="noticeIdx", direction = Sort.Direction.DESC)Pageable pageable) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray noticeArr = new JsonArray();

        List<NoticeDto> list = noticeService.noticeList(pageable);

        Gson gson = new Gson();
        for(NoticeDto noticeList: list) {
            noticeArr.add(gson.toJsonTree(noticeList));
        }

        data.add("notice_list", noticeArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

}
