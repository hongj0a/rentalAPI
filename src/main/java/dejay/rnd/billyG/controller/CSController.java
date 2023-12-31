package dejay.rnd.billyG.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.InquiryDto;

import java.io.File;
import java.io.IOException;

import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.OneToOneInquiryService;
import dejay.rnd.billyG.util.FrontUtil;
import dejay.rnd.billyG.service.UserMining;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class CSController {
    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final FaqRepository faqRepository;
    private final OneToOneInquiryRepository oneToOneInquiryRepository;
    private final UserRepository userRepository;
    private final OneToOneInquiryService oneToOneInquiryService;
    private final FileUploadService uploadService;
    private final InquiryImageRepository inquiryImageRepository;
    private final TermsRepository termsRepository;
    private final UserMining userMining;
    private final UserRepositories userRepositories;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;

    public CSController(NoticeRepository noticeRepository, CategoryRepository categoryRepository, FaqRepository faqRepository, OneToOneInquiryRepository oneToOneInquiryRepository, UserRepository userRepository, OneToOneInquiryService oneToOneInquiryService, FileUploadService uploadService, InquiryImageRepository inquiryImageRepository, TermsRepository termsRepository, UserMining userMining, UserRepositories userRepositories, AmazonS3Client amazonS3Client) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
        this.faqRepository = faqRepository;
        this.oneToOneInquiryRepository = oneToOneInquiryRepository;
        this.userRepository = userRepository;
        this.oneToOneInquiryService = oneToOneInquiryService;
        this.uploadService = uploadService;
        this.inquiryImageRepository = inquiryImageRepository;
        this.userRepositories = userRepositories;
        this.amazonS3Client = amazonS3Client;
        this.termsRepository = termsRepository;
        this.userMining = userMining;
    }

    @GetMapping("/getNotice")
    public ResponseEntity<JsonObject> getNoticeList(HttpServletRequest req, @PageableDefault(size = 10, sort = "noticeIdx", direction = Sort.Direction.DESC) Pageable pageable) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray noticeArr = new JsonArray();

        Page<Notice> list = noticeRepository.findByDeleteYnAndActiveYnAndActiveAtLessThanEqual(false, true, FrontUtil.getNowDate(), pageable);
        List<Notice> size = noticeRepository.findAllByDeleteYnAndActiveYnAndActiveAtLessThanEqual(false, true, FrontUtil.getNowDate());

        list.forEach(
                noti -> {
                    JsonObject no = new JsonObject();
                    String type = "";
                    no.addProperty("noticeIdx", noti.getNoticeIdx());
                    switch (noti.getNoticeType()) {
                        case "0":
                            type = "공지";
                            break;
                        case "1":
                            type = "이벤트";
                            break;
                        default:
                            type = "채용";
                            break;
                    }
                    no.addProperty("noticeType", type);
                    no.addProperty("title", noti.getTitle());
                    no.addProperty("content", StringEscapeUtils.unescapeHtml4(noti.getContent()));
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

        List<User> lists = userRepositories.findByAllUsers();

        System.out.println("#####################");
        List<Category> list = categoryRepository.findAllByCategoryTypeAndOrderNumNotInOrderByOrderNum(csType, new int[]{9999});

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

        List<Category> list = categoryRepository.findAllByCategoryTypeAndOrderNumNotInOrderByOrderNum("2", new int[]{9999});
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
                    //faq.addProperty("title", faqs.getTitle());
                    faq.addProperty("faqQuestion", faqs.getContent());

                    if (faqs.getAnswer() != null) {
                        faq.addProperty("answer", StringEscapeUtils.unescapeHtml4(faqs.getAnswer()));
                    } else {
                        faq.addProperty("answer", "");
                    }

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
        User findUser = userMining.getUserInfo(acToken);

        Page<OneToOneInquiry> list = oneToOneInquiryRepository.findAllByUser_userIdxAndDeleteYnOrderByCreateAtDesc(findUser.getUserIdx(), false, pageable);
        List<OneToOneInquiry> size = oneToOneInquiryRepository.findAllByUser_userIdxAndDeleteYn(findUser.getUserIdx(), false);


        list.forEach(
                ones -> {
                    JsonObject oto = new JsonObject();
                    JsonArray imgArr = new JsonArray();

                    String statusStr = "";

                    oto.addProperty("oneIdx", ones.getOneIdx());
                    if (ones.getCategory().getOrderNum() == 9999) {
                        oto.addProperty("typeName", "알 수 없음");
                    } else {
                        oto.addProperty("typeName", ones.getCategory().getName());
                    }

                    oto.addProperty("title", ones.getTitle());


                    switch (ones.getStatus()) {
                        case "0":
                            statusStr = "답변대기";
                            break;
                        case "1":
                            statusStr = "답변중";
                            break;
                        case "2":
                            statusStr = "답변완료";
                        default:
                            System.out.println("CSController.getInquiry");
                            break;
                    }


                    oto.addProperty("status", statusStr);

                    List<InquiryImage> findImg = inquiryImageRepository.findByOneToOneInquiry_oneIdx(ones.getOneIdx());
                    if (findImg.size() != 0) {
                        findImg.forEach(
                                img -> {
                                    JsonObject iObj = new JsonObject();
                                    iObj.addProperty("imageSeq", img.getImageIdx());
                                    iObj.addProperty("imageUrl", img.getImageUrl());
                                    imgArr.add(iObj);
                                }
                        );

                    }

                    oto.add("images", imgArr);
                    oto.addProperty("regDate", ones.getCreateAt().getTime());
                    oto.addProperty("content", StringEscapeUtils.unescapeHtml4(ones.getContent()));
                    
                    // 답변완료일때만 답변보이기
                    if (ones.getStatus().equals("2")) {
                        oto.addProperty("answerContent", StringEscapeUtils.unescapeHtml4(ones.getAnswerContent()));
                    } else {
                        oto.addProperty("answerContent", "");
                    }

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
                                                HttpServletRequest req) throws AppException, ParseException, IOException {
        JsonObject data = new JsonObject();

        System.out.println("CSController.setInquiry111111");

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        Category findCategory = categoryRepository.getOne(Long.valueOf(categoryIdx));

        OneToOneInquiry one = new OneToOneInquiry();
        one.setTitle(title);
        one.setContent(content);
        one.setUser(findUser);
        one.setStatus("0");
        one.setCategory(findCategory);

        OneToOneInquiry oneToOneInquiry = oneToOneInquiryService.insertOne(one);

        System.out.println("images.size() = " + images.size());
        for (int i = 0; i < images.size(); i++) {

            if (!StringUtils.isEmpty(images.get(i).getOriginalFilename())) {
                InquiryImage inquiryImage = new InquiryImage();
                ImageFile file = uploadService.upload(images.get(i));

                inquiryImage.setOneToOneInquiry(oneToOneInquiry);
                inquiryImage.setImageUrl(file.getFileName());

                inquiryImageRepository.save(inquiryImage);
            }


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
        User findUser = userMining.getUserInfo(acToken);

        OneToOneInquiry findInquiry = oneToOneInquiryRepository.getOne(inquiryDto.getOneIdx());
        findInquiry.setDeleteYn(true);
        findInquiry.setUpdator(findUser.getEmail());

        oneToOneInquiryService.updateInquiry(findInquiry);

        List<InquiryImage> imglist = inquiryImageRepository.findByOneToOneInquiry_oneIdx(inquiryDto.getOneIdx());

        String storeFileUrl ;
        for (int i = 0; i < imglist.size(); i++) {

            String key = "image/" + imglist.get(i).getImageUrl();
            storeFileUrl = amazonS3Client.getUrl(bucketName, key).toString();

            File file = new File(storeFileUrl);

            if (file.exists()) {
                file.delete();
            }

            inquiryImageRepository.delete(imglist.get(i));
        }

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }


    @GetMapping("/isNoticeCheck")
    public ResponseEntity<JsonObject> isNoticeCheck(HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        Notice findNotice = noticeRepository.findByStartAtLessThanEqualAndEndAtGreaterThanEqualAndViewType(FrontUtil.getNowDate(), FrontUtil.getNowDate(), "20");

        if (findNotice != null) {

            data.addProperty("title", findNotice.getTitle());
            data.addProperty("content", StringEscapeUtils.unescapeHtml4(findNotice.getContent()));
            data.addProperty("regDate", findNotice.getCreateAt().getTime());

        }
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
    }


    @GetMapping("/getTerms")
    public ResponseEntity<JsonObject> getUseTerms(@RequestParam (value = "type") Integer type,
                                                  @RequestParam (value = "termsIdx", required = false) Long termsIdx,
                                                  HttpServletRequest req) throws AppException {
        JsonObject data = new JsonObject();
        JsonArray termsArr = new JsonArray();

        List<Terms> terms = termsRepository.findByReservationDateLessThanEqualAndDeleteYnAndTypeOrderByCreateAtDesc(FrontUtil.getNowDate(),false, type);


        for (int i = 0; i < terms.size(); i++) {
            JsonObject tr = new JsonObject();
            if (termsIdx != null) {
                if (terms.get(i).getTermsIdx() == termsIdx) {
                    tr.addProperty("title", terms.get(i).getTitle());
                    tr.addProperty("content", StringEscapeUtils.unescapeHtml4(terms.get(i).getContent()));
                    tr.addProperty("termsIdx", terms.get(i).getTermsIdx());
                    tr.addProperty("createAt", String.valueOf(terms.get(i).getCreateAt()));
                }
            } else {
                tr.addProperty("title", terms.get(0).getTitle());
                tr.addProperty("content", StringEscapeUtils.unescapeHtml4(terms.get(0).getContent()));
                tr.addProperty("termsIdx", terms.get(0).getTermsIdx());
                tr.addProperty("createAt", String.valueOf(terms.get(0).getCreateAt()));
            }
            tr.addProperty("termsIdx", terms.get(i).getTermsIdx());
            tr.addProperty("title", terms.get(i).getTitle());
            tr.addProperty("content", StringEscapeUtils.unescapeHtml4(terms.get(i).getContent()));
            tr.addProperty("createAt", String.valueOf(terms.get(i).getCreateAt()));

            termsArr.add(tr);
        }

        data.add("terms", termsArr);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }



}
