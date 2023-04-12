package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.InquiryDto;
import dejay.rnd.billyG.dto.MainDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.OneToOneInquiryService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CSController {
    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final FaqRepository faqRepository;
    private final OneToOneInquiryRepository oneToOneInquiryRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final OneToOneInquiryService oneToOneInquiryService;
    private final FileUploadService uploadService;
    private final InquiryImageRepository inquiryImageRepository;

    @GetMapping("/getNotice")
    public ResponseEntity<JsonObject> getNoticeList(HttpServletRequest req, @PageableDefault(size = 10, sort = "noticeIdx", direction = Sort.Direction.DESC) Pageable pageable) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray noticeArr = new JsonArray();

        Page<Notice> list = noticeRepository.findByDeleteYn(false, pageable);
        List<Notice> size = noticeRepository.findAllByDeleteYn(false);

        list.forEach(
                noti -> {
                    JsonObject no = new JsonObject();
                    String type = "";
                    no.addProperty("noticeIdx", noti.getNoticeIdx());
                    switch (noti.getNoticeType()) {
                        case 0:
                            type = "공지";
                            break;
                        case 1:
                            type = "이벤트";
                            break;
                        default:
                            type = "채용";
                            break;
                    }
                    no.addProperty("noticeType", type);
                    no.addProperty("title", noti.getTitle());
                    no.addProperty("content", noti.getContent());
                    no.addProperty("regDate", noti.getCreateAt().getTime());

                    noticeArr.add(no);
                }
        );

        data.add("noticeList", noticeArr);
        data.addProperty("totalCount", size.size());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getType")
    public ResponseEntity<JsonObject> getCSType(@RequestParam (value = "csType") String csType,
                                                HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray faqArr = new JsonArray();

        List<Category> list = categoryRepository.findAllByCategoryTypeOrderByOrderNum(csType);

        list.forEach(
                faqs -> {
                    JsonObject faq = new JsonObject();

                    faq.addProperty("typeIdx", faqs.getCategoryIdx());
                    faq.addProperty("typeName", faqs.getName());

                    faqArr.add(faq);
                }
        );

        data.add("typeList", faqArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getFaqList")
    public ResponseEntity<JsonObject> getFaqList(@RequestParam (value = "type") long type, Pageable pageable,
                                                 HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray faqArr = new JsonArray();

        /**
         * [faq type]
         * 0 : 전체
         * 그 외 : faqArr 에서 넘겨줬던 faqTypeIdx값을 받으면 됨
         * =================================
         */

        List<Category> list = categoryRepository.findAllByCategoryTypeOrderByOrderNum("2");
        ArrayList<Long> f_keys = new ArrayList<>();

        if (type == 0) {
            for (int i = 0; i < list.size(); i++) {
                f_keys.add(list.get(i).getCategoryIdx());
            }
        } else {
            f_keys.add(type);
        }

        Page<Faq> faqList = faqRepository.findAllByCategory_categoryIdxInAndActiveYnOrderByCreateAtDesc(f_keys, true, pageable);
        List<Faq> faqSize = faqRepository.findAllByCategory_categoryIdxInAndActiveYn(f_keys, true);

        faqList.forEach(
                faqs -> {
                    JsonObject faq = new JsonObject();

                    faq.addProperty("faqIdx", faqs.getFaqIdx());
                    faq.addProperty("faqTypeName", faqs.getCategory().getName());
                    faq.addProperty("title", faqs.getTitle());
                    faq.addProperty("content", faqs.getContent());

                    faqArr.add(faq);
                }
        );

        data.add("faqList", faqArr);
        data.addProperty("totalCount", faqSize.size());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    @GetMapping("/getInquiry")
    public ResponseEntity<JsonObject> getInquiry(Pageable pageable, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray faqArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Page<OneToOneInquiry> list = oneToOneInquiryRepository.findAllByUser_userIdxAndDeleteYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);
        List<OneToOneInquiry> size = oneToOneInquiryRepository.findAllByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);


        list.forEach(
                ones -> {
                    JsonObject oto = new JsonObject();
                    JsonArray imgArr = new JsonArray();

                    String statusStr = "답변대기";

                    oto.addProperty("oneIdx", ones.getOneIdx());
                    oto.addProperty("typeName", ones.getCategory().getName());
                    oto.addProperty("title", ones.getTitle());
                    if (ones.getStatus() == 1) {
                        statusStr = "답변완료";
                        Answer answer = answerRepository.findByOneToOneInquiry_OneIdx(ones.getOneIdx());
                        oto.addProperty("answerIdx", answer.getAnswerIdx());
                        oto.addProperty("answerContent", answer.getAnswerContent());
                    } else {
                        oto.addProperty("answerIdx", "");
                        oto.addProperty("answerContent", "");
                    }
                    oto.addProperty("status", statusStr);

                    List<InquiryImage> findimg = inquiryImageRepository.findByOneToOneInquiry_oneIdx(ones.getOneIdx());
                    findimg.forEach(
                            img -> {
                                JsonObject iObj = new JsonObject();
                                iObj.addProperty("imageSeq", img.getImageIdx());
                                iObj.addProperty("imageUrl", img.getImageUrl());
                                imgArr.add(iObj);
                            }
                    );


                    oto.addProperty("regDate", ones.getCreateAt().getTime());
                    oto.addProperty("content", ones.getContent());
                    oto.add("images", imgArr);
                    faqArr.add(oto);
                }
        );

        data.add("oneList", faqArr);
        data.addProperty("totalCount", size.size());
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }

    //1:1문의 등록
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/setInquiry", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> setInquiry(@RequestParam (value = "images") List<MultipartFile> images,
                                                @RequestParam (value = "title") String title,
                                                @RequestParam (value = "content") String content,
                                                @RequestParam (value = "categoryIdx") String categoryIdx,
                                                HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        Category findCategory = categoryRepository.getOne(Long.valueOf(categoryIdx));

        OneToOneInquiry one = new OneToOneInquiry();
        one.setTitle(title);
        one.setContent(content);
        one.setUser(findUser);
        one.setStatus(0);
        one.setCategory(findCategory);

        OneToOneInquiry oneToOneInquiry = oneToOneInquiryService.insertOne(one);

        for (int i = 0; i < images.size(); i++) {
            InquiryImage inquiryImage = new InquiryImage();
            ImageFile file = uploadService.upload(images.get(i));

            inquiryImage.setOneToOneInquiry(oneToOneInquiry);
            inquiryImage.setImageUrl(file.getFileName());

            inquiryImageRepository.save(inquiryImage);
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    //1:1문의 삭제
    @PostMapping("/delInquiry")
    public ResponseEntity<JsonObject> delInquiry(@RequestBody InquiryDto inquiryDto,
                                                 HttpServletRequest req) throws ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        OneToOneInquiry findInquiry = oneToOneInquiryRepository.getOne(inquiryDto.getOneIdx());
        findInquiry.setDeleteYn(true);
        findInquiry.setUpdator(findUser.getEmail());

        oneToOneInquiryService.updateInquiry(findInquiry);

        List<InquiryImage> imglist = inquiryImageRepository.findByOneToOneInquiry_oneIdx(inquiryDto.getOneIdx());

        for (int i = 0; i < imglist.size(); i++) {
            inquiryImageRepository.delete(imglist.get(i));
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }


}
