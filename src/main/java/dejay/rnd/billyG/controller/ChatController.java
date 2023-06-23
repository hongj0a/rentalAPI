package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.ChatContentDto;
import dejay.rnd.billyG.dto.ChatRoomDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.repositoryImpl.ChatRepositories;
import dejay.rnd.billyG.repositoryImpl.TransactionRepositories;
import dejay.rnd.billyG.service.*;
import dejay.rnd.billyG.util.FrontUtil;
import dejay.rnd.billyG.service.UserMining;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 채팅방 상세에 채팅상대가 차단대상인지 조회해서
 * 차단여부 플래그값 리턴해주기
 */
@Controller
@RequestMapping("/")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final RentalImageRepository rentalImageRepository;
    private final RentalRepository rentalRepository;
    private final RentalService rentalService;
    private final ChatContentRepository chatContentRepository;
    private final SimpMessagingTemplate template;
    private final FileUploadService uploadService;
    private final TransactionRepository transactionRepository;
    private final ChatImageRepository chatImageRepository;
    private final ChatRepositories chatRepositories;
    private final TransactionService transactionService;
    private final TransactionRepositories transactionRepositories;
    private final BellScheduleRepositry bellScheduleRepositry;
    private final PushService pushService;
    private final LikeRepository likeRepository;
    private final UserMining userMining;


    public ChatController(ChatService chatService, UserRepository userRepository, ChatRepository chatRepository, RentalImageRepository rentalImageRepository, RentalRepository rentalRepository, RentalService rentalService, ChatContentRepository chatContentRepository, SimpMessagingTemplate template, FileUploadService uploadService, TransactionRepository transactionRepository, ChatImageRepository chatImageRepository, ChatRepositories chatRepositories, TransactionService transactionService, TransactionRepositories transactionRepositories, BellScheduleRepositry bellScheduleRepositry, PushService pushService, LikeRepository likeRepository, UserMining userMining) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
        this.chatContentRepository = chatContentRepository;
        this.template = template;
        this.uploadService = uploadService;
        this.transactionRepository = transactionRepository;
        this.chatImageRepository = chatImageRepository;
        this.chatRepositories = chatRepositories;
        this.transactionService = transactionService;
        this.transactionRepositories = transactionRepositories;
        this.bellScheduleRepositry = bellScheduleRepositry;
        this.pushService = pushService;
        this.likeRepository = likeRepository;
        this.userMining = userMining;
    }


    @Transactional(rollbackFor = Exception.class)
    @MessageMapping(value = "/chat/message")
    public void message(@Payload ChatContentDto contentDto) throws java.text.ParseException {

        Executor executor = Executors.newFixedThreadPool(30);
        LocalDateTime date = LocalDateTime.now();
        long now_date = Timestamp.valueOf(date).getTime();

        contentDto.setStep(contentDto.getStep());

        TransactionHistory history = new TransactionHistory();

        //일반 메시지일때 보내는사람 userIdx, 받는사람 rentalIdx
        //userIdx가 chatroomIdx에 sender? reciever? 확인해서
        //sender이면 reciever에게 메시지 푸시
        //receiver이면 sender에게 메시지 푸시

        User findUser = userRepository.findByUserIdx(contentDto.getUserIdx());
        ChatRoom findRoom = chatRepository.findByChatRoomIdx(contentDto.getChatRoomIdx());
        Rental findRental = rentalRepository.findByRentalIdx(findRoom.getRental().getRentalIdx());
        User renter = userRepository.findByUserIdx(findRoom.getFromUser().getUserIdx());


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String parseDate = dateFormat.format(FrontUtil.getNowDate());
        Date beforeDate = dateFormat.parse(parseDate);

        List<Transaction> trs = transactionRepository.findByCreateAtGreaterThanEqualAndCreateAtLessThanEqual(beforeDate, FrontUtil.getNowDate());
        int len = (int) (Math.log10(trs.size()) + 1);

        //system 메시지인 경우
        if (contentDto.isSystemYn() == true) {
            ChatContent chat = new ChatContent();
            chat.setChatRoom(findRoom);
            chat.setUser(findUser);
            chat.setContent(contentDto.getMessage());
            chat.setSystemYn(true);

            chatService.insert(chat);
            contentDto.setSystemYn(true);
            if (contentDto.getTransactionIdx() != 0) {
                Transaction transaction = transactionRepository.findByTransactionIdx(contentDto.getTransactionIdx());
                contentDto.setStatus(String.valueOf(transaction.getOwnerStatus()));
            }
            contentDto.setRegDate(now_date);

        } else if (contentDto.isSystemYn() == false){
            //채팅 메시지가 있는경우, 새로운 메시지 알림
            if (findUser != null && contentDto.getMessage() != null) {
                ChatContent chat = new ChatContent();
                chat.setUser(findUser);
                chat.setChatRoom(findRoom);
                chat.setContent(contentDto.getMessage());
                ChatContent chatContent = chatService.insert(chat);

                findRoom.setLastChatMessage(contentDto.getMessage());
                findRoom.setUpdator(findUser.getEmail());
                findRoom.setReadYn(false);

                if (findRoom.getVisibleTo() != 0) {
                    findRoom.setVisibleTo(0L);
                }

                chatService.updateChatRoom(findRoom);

                CompletableFuture.runAsync(() -> {
                    try {
                        //렌탈러에게
                        if (findUser.getUserIdx() == findRoom.getToUser().getUserIdx()) {
                            if (findRoom.getFromUser().isChatNoticeYn() == true) {
                                pushService.sendPush(new Long[]{findRoom.getFromUser().getUserIdx()}, findRental.getRentalIdx(), findRoom.getChatRoomIdx(),
                                        50, "새로운 메시지", "새로운 메시지가 있습니다.");
                            }
                        } else {
                            if (findRoom.getToUser().isChatNoticeYn() == true) {
                                pushService.sendPush(new Long[]{findRoom.getToUser().getUserIdx()}, findRental.getRentalIdx(), findRoom.getChatRoomIdx(),
                                        50, "새로운 메시지", "새로운 메시지가 있습니다.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + ": hi");
                }, executor);

                if (contentDto.getTransactionIdx() != 0) {
                    Transaction transaction = transactionRepository.findByTransactionIdx(contentDto.getTransactionIdx());
                    contentDto.setStatus(String.valueOf(transaction.getOwnerStatus()));
                } else {
                    contentDto.setStatus(String.valueOf(10));
                }

                contentDto.setMessageSeq(chatContent.getChatIdx());
                contentDto.setNickName(findUser.getNickName());
                contentDto.setImage(findUser.getProfileImageUrl());
                contentDto.setRegDate(now_date);
            }
        }

        if (StringUtils.isEmpty(contentDto.getMessage()) && contentDto.getTransactionIdx() != 0 && contentDto.isSystemYn() != true) {
            //변경되는 내용 다시저장
            Transaction findTr = transactionRepository.findByTransactionIdx(contentDto.getTransactionIdx());

            if (contentDto.getStep()== 0 ) {

                if (findTr.getUser().isActivityNoticeYn() == true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            //렌탈 오너가 취소를 눌렀을 때
                            pushService.sendPush(new Long[]{findTr.getUser().getUserIdx()}, findTr.getRental().getRentalIdx(), findRoom.getChatRoomIdx(),
                                    50, "렌탈매칭 취소", findTr.getRental().getUser().getNickName() + " 님이 매칭을 취소 했습니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);
                }


                if (findTr.getRental().getUser().isActivityNoticeYn() == true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            //렌탈러가 취소를 눌렀을 때
                            pushService.sendPush(new Long[]{findTr.getRental().getUser().getUserIdx()}, findTr.getRental().getRentalIdx(), findRoom.getChatRoomIdx(),
                                    50, "렌탈매칭 취소", findTr.getUser().getNickName() + " 님이 매칭을 취소 했습니다.");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);

                }

                findTr.setCancelYn(true);
                findTr.setCancelAt(FrontUtil.getNowDate());
                findTr.setStatusAt(FrontUtil.getNowDate());

                history.setCancelAt(FrontUtil.getNowDate());
                history.setStatusAt(FrontUtil.getNowDate());
                history.setCancelYn(true);

                findRoom.setFirstYn(true);
                chatService.updateChatRoom(findRoom);

                contentDto.setStatus(String.valueOf(10));
                contentDto.setTransactionIdx(0L);

            } else if (contentDto.getStep()==1) {

                history.setOwnerStatus(findTr.getOwnerStatus() + 10);
                history.setRenterStatus(findTr.getRenterStatus() + 10);
                history.setUpdateAt(FrontUtil.getNowDate());
                history.setStatusAt(FrontUtil.getNowDate());

                findTr.setOwnerStatus(findTr.getOwnerStatus() + 10);
                findTr.setRenterStatus(findTr.getRenterStatus() + 10);
                findTr.setStatusAt(FrontUtil.getNowDate());

                if (findTr.getOwnerStatus() == 30 && findTr.getRental().getUser().isActivityNoticeYn() == true) {

                    CompletableFuture.runAsync(() -> {
                        try {
                            //렌탈러가 매칭완료를 눌렀을 때 렌탈오너에게
                            pushService.sendPush(new Long[]{findTr.getRental().getUser().getUserIdx()}, findTr.getRental().getRentalIdx(), findRoom.getChatRoomIdx(),
                                    50, "렌탈매칭 완료", findTr.getUser().getNickName() + " 님과 렌탈매칭 되었습니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);

                }

                if (findTr.getOwnerStatus() == 40 && findTr.getRental().getUser().isActivityNoticeYn() == true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            //물품인수가 되었을 때
                            pushService.sendPush(new Long[]{findTr.getRental().getUser().getUserIdx()}, findTr.getRental().getRentalIdx(), findRoom.getChatRoomIdx(),
                                    50, "물품인수 완료", findTr.getUser().getNickName() + " 님이 물품인수를 확인 했습니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);

                }

                if (findTr.getOwnerStatus() == 50 && findTr.getRental().getUser().isActivityNoticeYn() == true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            //물품반납이 되었을 때
                            pushService.sendPush(new Long[]{findTr.getRental().getUser().getUserIdx()}, findTr.getRental().getRentalIdx(), findRoom.getChatRoomIdx(),
                                    50, "물품반납 완료", findTr.getUser().getNickName() + " 님이 물품 반납을 했습니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);
                }

                contentDto.setStatus(String.valueOf(findTr.getOwnerStatus()));
            } else if (contentDto.getStep() == 2) {

                history.setOwnerStatus(70);
                history.setRenterStatus(70);
                history.setUpdateAt(FrontUtil.getNowDate());
                history.setCompleteAt(FrontUtil.getNowDate());
                history.setStatusAt(FrontUtil.getNowDate());

                findTr.setOwnerStatus(70);
                findTr.setRenterStatus(70);
                findTr.setCompleteAt(FrontUtil.getNowDate());
                findTr.setStatusAt(FrontUtil.getNowDate());

                findRoom.setFirstYn(true);
                chatService.updateChatRoom(findRoom);

                findRental.setStatus(1);
                rentalService.updateRental(findRental);


                if (findTr.getOwnerStatus() == 70 && findTr.getRental().getUser().isActivityNoticeYn() ==true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            //렌탈완료 알림 - 렌탈오너에게
                            pushService.sendPush(new Long[]{findTr.getRental().getUser().getUserIdx()}, findTr.getUser().getUserIdx(), findRoom.getChatRoomIdx(),
                                    50, "렌탈완료", findTr.getUser().getNickName() + " 님과의 렌탈거래가 완료 되었습니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);
                }


                if (findTr.getOwnerStatus() == 70 && findTr.getUser().isActivityNoticeYn() == true) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            //렌탈완료 알림 - 렌탈러에게
                            pushService.sendPush(new Long[]{findTr.getUser().getUserIdx()}, findTr.getRental().getRentalIdx(), findRoom.getChatRoomIdx(),
                                    50, "렌탈완료", findTr.getRental().getTitle() + " 물품에 대한 렌탈이 완료 되었습니다.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": hi");
                    }, executor);
                }


                //렌탈가능시 알림받기 유저들에게 렌탈가능 알림
                List<BellSchedule> bells = bellScheduleRepositry.findAllByRental_rentalIdxAndDeleteYnAndUser_ActivityNoticeYn(findRental.getRentalIdx(), false,true);
                Long[] hosts = new Long[bells.size()];

                for (int i = 0; i < hosts.length; i++) {
                    hosts[i] = bells.get(i).getUser().getUserIdx();
                }


                CompletableFuture.runAsync(() -> {
                    try {
                        pushService.sendPush(hosts, findUser.getUserIdx(), findRental.getRentalIdx(),
                                10, "[알림] 렌탈가능 물품", "알림 설정하신 " + findRental.getTitle() + " 상품이 렌탈 가능한 상태로 변경되었습니다.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + ": hi");
                }, executor);


                //해당게시글 좋아요 누른사람들에게 렌탈가능 알림
                List<Likes> likes = likeRepository.findAllByRental_rentalIdxAndDeleteYnAndUser_ActivityNoticeYn(findRental.getRentalIdx(), false, true);
                Long[] users = new Long[likes.size()];

                for (int i = 0; i < users.length; i++) {
                    users[i] = likes.get(i).getUser().getUserIdx();
                }

                CompletableFuture.runAsync(() -> {
                    try {
                        pushService.sendPush(users, findUser.getUserIdx(), findRental.getRentalIdx(),
                                10, "게시글 상태 변경", "회원님께서 좋아요한 게시글 "+findRental.getTitle()+" 상품이 렌탈가능 합니다.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + ": hi");
                }, executor);


                contentDto.setTransactionIdx(0L);
                contentDto.setStatus(String.valueOf(10));

            }

            transactionService.updateTransacion(findTr);

            history.setRentalIdx(findTr.getRental().getRentalIdx());
            history.setTransactionIdx(findTr.getTransactionIdx());


        } else if (contentDto.getTransactionIdx() == 0 && contentDto.isSystemYn() != true) {
            //null, 새로 만들어야 하는 시점. 히스토리도 똑같이 입력
                if (contentDto.getStep()==1) {

                    Transaction tr = new Transaction();
                    tr.setOwnerStatus(20);
                    tr.setRenterStatus(20);
                    tr.setCancelYn(false);
                    tr.setRental(findRoom.getRental());
                    tr.setUser(renter);

                    findRental.setStatus(2);
                    rentalService.updateRental(findRental);

                    switch (len) {
                        case 0 :
                            //거래번호 00000
                            tr.setTransactionNum(parseDate + "00000" + trs.size() +1);
                            break;
                        case 1 :
                            //거래번호 00000
                            tr.setTransactionNum(parseDate + "00000" + trs.size() +1);
                            break;
                        case 2 :
                            //거래번호 0000
                            tr.setTransactionNum(parseDate + "0000" + trs.size() +1);
                            break;
                        case 3 :
                            //거래번호 000
                            tr.setTransactionNum(parseDate + "000" + trs.size() +1);
                            break;
                        case 4 :
                            //거래번호 00
                            tr.setTransactionNum(parseDate + "00" + trs.size() +1);
                            break;
                        case 5 :
                            //거래번호 0
                            tr.setTransactionNum(parseDate + "0" + trs.size() +1);
                            break;
                        default :
                            //거래번호
                            tr.setTransactionNum(parseDate + trs.size() +1);
                            break;
                    }
                    tr.setStatusAt(FrontUtil.getNowDate());

                    Transaction transaction = transactionService.insertTransaction(tr);

                    history.setTransactionIdx(transaction.getTransactionIdx());
                    history.setTransactionNum(transaction.getTransactionNum());
                    history.setOwnerStatus(transaction.getOwnerStatus());
                    history.setRenterStatus(transaction.getRenterStatus());
                    history.setCancelYn(false);
                    history.setRentalIdx(findRoom.getRental().getRentalIdx());
                    history.setStatusAt(FrontUtil.getNowDate());
                    history.setUpdateAt(FrontUtil.getNowDate());
                    history.setUserIdx(renter.getUserIdx());

                    contentDto.setStatus(String.valueOf(20));
                    contentDto.setTransactionIdx(tr.getTransactionIdx());

                    if (transaction.getUser().isActivityNoticeYn() == true) {
                        CompletableFuture.runAsync(() -> {
                            try {
                                //렌탈오너가 렌탈매칭을 눌렀을 때 렌탈러에게
                                pushService.sendPush(new Long[]{transaction.getUser().getUserIdx()}, transaction.getRental().getUser().getUserIdx(), findRoom.getChatRoomIdx(),
                                        50, "렌탈매칭 완료", transaction.getRental().getTitle()+ " 물품에 대한 렌탈이 매칭 되었습니다.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(Thread.currentThread().getName() + ": hi");
                        }, executor);
                    }


            }

        }

        history.setCreateAt(FrontUtil.getNowDate());
        history.setUpdator(findUser.getEmail());

        //최종 히스토리 set
        transactionService.insertHistory(history);

        System.out.println("contentDto 최종!!!!!! === " + contentDto.getStatus());
        template.convertAndSend("/sub/isRoomExistCheck" + contentDto.getChatRoomIdx(), contentDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value ="/api/setChatImage", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> setChatImage(@RequestParam (value = "images", required = false) List<MultipartFile> images,
                                                   @RequestParam (value = "roomIdx") Long roomIdx,
                                                   HttpServletRequest req) throws AppException, ParseException, IOException {
        JsonObject data = new JsonObject();
        JsonArray imgArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        ChatRoom findRoom = chatRepository.findByChatRoomIdx(roomIdx);

        ChatContent content = new ChatContent();
        content.setImageYn(true);
        content.setChatRoom(findRoom);
        content.setUser(findUser);

        ChatContent chat = chatService.insert(content);

        findRoom.setLastChatMessage("이미지를 보냈습니다.");
        chatService.updateChatRoom(findRoom);

        for (int i = 0; i < images.size(); i++) {
            if (!StringUtils.isEmpty(images.get(i).getOriginalFilename())) {
                ChatImage ci = new ChatImage();
                JsonObject img = new JsonObject();
                ImageFile file = uploadService.upload(images.get(i));

                ci.setChatContent(chat);
                ci.setImageUrl(file.getFileName());

                ChatImage chatImage = chatImageRepository.save(ci);
                img.addProperty("imageUrl", chatImage.getImageUrl());
                imgArr.add(img);
            }
        }

        // 이미지 파일명 배열사이즈만큼 구해서 리턴

        data.addProperty("messageSeq", chat.getChatIdx());
        data.addProperty("userIdx", findUser.getUserIdx());
        data.addProperty("regDate", chat.getCreateAt().getTime());
        data.add("images", imgArr);
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }


    @GetMapping("/getChatRooms")
    public ResponseEntity<JsonObject> getChatRooms(Pageable pageable, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray roomArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        Page<ChatRoom> rooms = chatRepositories.findAll(findUser.getUserIdx(), findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()}, pageable);
        List<ChatRoom> totalRooms = chatRepositories.getTotalCount(findUser.getUserIdx(), findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()});

        rooms.forEach(
                rm -> {
                    JsonObject rms = new JsonObject();
                    rms.addProperty("roomIdx", rm.getChatRoomIdx());
                    if (rm.getToUser().getUserIdx() == findUser.getUserIdx()) {
                        rms.addProperty("userSeq", rm.getFromUser().getUserIdx());
                        rms.addProperty("imageUrl", rm.getFromUser().getProfileImageUrl());
                        rms.addProperty("nickName", rm.getFromUser().getNickName());
                    } else if (rm.getFromUser().getUserIdx() == findUser.getUserIdx()) {
                        rms.addProperty("userSeq", rm.getToUser().getUserIdx());
                        rms.addProperty("imageUrl", rm.getToUser().getProfileImageUrl());
                        rms.addProperty("nickName", rm.getToUser().getNickName());
                    }

                    rms.addProperty("lastMessage", rm.getLastChatMessage());
                    if (rm.getUpdator().equals(findUser.getEmail())) {
                        rms.addProperty("readYn", true);
                    } else {
                        rms.addProperty("readYn", rm.isReadYn());
                    }
                    List<RentalImage> img = rentalImageRepository.findByRental_rentalIdx(rm.getRental().getRentalIdx());
                    rms.addProperty("rentalIdx", rm.getRental().getRentalIdx());
                    if (img.size() != 0) {
                        rms.addProperty("rentalImage", img.get(0).getImageUrl());
                    } else {
                        rms.addProperty("rentalImage", "deletedImage.png");
                    }

                    rms.addProperty("regDate", rm.getUpdateAt().getTime());

                    if (rm.getRental().getStatus() == 2) {
                        rms.addProperty("exitYn", false);
                    } else {
                        rms.addProperty("exitYn", true);
                    }

                    roomArr.add(rms);
                }
        );
        data.add("roomList", roomArr);
        data.addProperty("totalCount", totalRooms.size());

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/setExit")
    public ResponseEntity<JsonObject> setExit(@RequestBody ChatRoomDto chatRoomDto, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        ChatRoom findChat = chatRepository.findByChatRoomIdx(chatRoomDto.getChatRoomIdx());
        Rental findRental = rentalRepository.findByRentalIdx(findChat.getRental().getRentalIdx());

        if (findRental.getStatus() == 2) {
            apiRes.setError(ErrCode.err_api_transaction_ing.code());
            apiRes.setMessage(ErrCode.err_api_transaction_ing.msg());
            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        } else {
            if (findChat != null) {
                if (findChat.getVisibleTo() != 0) {
                    findChat.setVisibleTo(-1L);
                } else if ( findChat.getVisibleTo() == 0){
                    findChat.setVisibleTo(findUser.getUserIdx());
                    if (findChat.getToUser().getUserIdx() == findUser.getUserIdx()) {
                        findChat.setToExitAt(FrontUtil.getNowDate());
                    } else {
                        findChat.setFromExitAt(FrontUtil.getNowDate());
                    }
                }
                findChat.setUpdator(findUser.getEmail());

                chatService.updateExit(findChat);
            }

        }



        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/delChatRoom")
    public ResponseEntity<JsonObject> delChatRoom(@RequestBody ChatRoomDto chatRoomDto, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        ChatRoom findChat = chatRepository.findByChatRoomIdx(chatRoomDto.getChatRoomIdx());

        if (findChat != null) {
            if (findChat.getLastChatMessage() == null) {
                chatRepository.delete(findChat);
            }
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/isRoomExistCheck")
    public ResponseEntity<JsonObject> isRoomExistCheck(@RequestParam(value="rentalIdx") Long rentalIdx,
                                                       @RequestParam(value="roomIdx", required = false) Long roomIdx,
                                                       @RequestParam(value = "readFlag", required = false) int readFlag, Pageable pageable, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray conArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        User findUser = userMining.getUserInfo(acToken);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Rental findRental = rentalRepository.getOne(rentalIdx);

        ChatRoom findChat;
        if (roomIdx == 0) {
            findChat = chatRepositories.getChat(findUser.getUserIdx(), findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()}, rentalIdx);
            if (findChat != null) {
                findChat.setReadYn(true);
            }
        } else {
            findChat = chatRepository.findByChatRoomIdx(roomIdx);
        }

        if (findChat != null) {
            if (readFlag == 1) {
                findChat.setReadYn(true);
            }
            if (findChat.isFirstYn() == true && (findChat.getRental().getUser().getUserIdx() == findUser.getUserIdx())) {
                data.addProperty("ownerFirstYn", findChat.isFirstYn());

                findChat.setFirstYn(false);

            } else {
                data.addProperty("ownerFirstYn", findChat.isFirstYn());
            }
            chatService.updateChatRoom(findChat);

            //나가기 이후 보여주기
            Page<ChatContent> chats = null;
            List<ChatContent> size = null;

            //오너나 렌터가 나가기 한 경우
            if (findChat.getVisibleTo() != 0 || findChat.getFromExitAt() != null || findChat.getToExitAt() != null) {

                System.out.println("findUser.getUserIdx() = " + findUser.getUserIdx());
                if (findChat.getToUser().getUserIdx() == findUser.getUserIdx()) {
                    //오너가 나가기 한 경우, 오너가 나가기 한 이 후로 채팅을 보여줘야 함
                    System.out.println("findChat = " + findChat.getToExitAt());
                    chats = chatContentRepository.findByChatRoom_chatRoomIdxAndCreateAtGreaterThanEqualOrderByCreateAtDesc(findChat.getChatRoomIdx(), findChat.getToExitAt(), pageable);
                    size = chatContentRepository.findByChatRoom_chatRoomIdxAndCreateAtGreaterThanEqual(findChat.getChatRoomIdx(), findChat.getToExitAt());

                } else if (findChat.getFromUser().getUserIdx() == findUser.getUserIdx()) {
                    //렌터가 나가기 한 경우, 렌터가 나가기 한 이 후로 채팅을 보여줘야 함
                    System.out.println("findChat.getFromExitAt() = " + findChat.getFromExitAt());
                    chats = chatContentRepository.findByChatRoom_chatRoomIdxAndCreateAtGreaterThanEqualOrderByCreateAtDesc(findChat.getChatRoomIdx(), findChat.getFromExitAt(), pageable);
                    size = chatContentRepository.findByChatRoom_chatRoomIdxAndCreateAtGreaterThanEqual(findChat.getChatRoomIdx(), findChat.getFromExitAt());

                }

            } else {
                //둘 다 나가기 안 한 경우
                chats = chatContentRepository.findByChatRoom_chatRoomIdxOrderByCreateAtDesc(findChat.getChatRoomIdx(), pageable);
                size = chatContentRepository.findByChatRoom_chatRoomIdxOrderByCreateAtDesc(findChat.getChatRoomIdx());

            }

            //채팅목록 뿌려주기
            chats.forEach(
                    ch -> {

                        JsonObject chs = new JsonObject();

                        chs.addProperty("messageSeq", ch.getChatIdx());
                        chs.addProperty("userIdx", ch.getUser().getUserIdx());

                        if (findUser.getUserIdx() != ch.getUser().getUserIdx()) {
                            chs.addProperty("image", ch.getUser().getProfileImageUrl());
                            chs.addProperty("nickName", ch.getUser().getNickName());
                        }
                        if (ch.isImageYn() == true) {
                            JsonArray imgArr = new JsonArray();
                            List<ChatImage> chatImages = chatImageRepository.findByChatContent_ChatIdx(ch.getChatIdx());
                            chatImages.forEach(
                                    ci -> {
                                        JsonObject cis = new JsonObject();
                                        cis.addProperty("imageSeq", ci.getImageIdx());
                                        cis.addProperty("imageUrl", ci.getImageUrl());
                                        cis.addProperty("regDate", ci.getCreateAt().getTime());

                                        imgArr.add(cis);
                                    }
                            );
                            chs.add("images", imgArr);

                        }
                        chs.addProperty("systemYn", ch.isSystemYn());
                        chs.addProperty("message", ch.getContent());
                        chs.addProperty("regDate", ch.getCreateAt().getTime());

                        conArr.add(chs);
                    }
            );


            if (findUser.getUserIdx() == findChat.getToUser().getUserIdx()) {
                data.addProperty("nickName", findChat.getFromUser().getNickName());
            } else {
                data.addProperty("nickName", findChat.getToUser().getNickName());
            }
            data.add("contents", conArr);
            data.addProperty("totalCount", size.size());
            data.addProperty("roomIdx", findChat.getChatRoomIdx());
            //거래이력 조회하기 (게시글사용자, 로그인사용자, 렌탈인덱스로 조회되는 트랜잭션있는지 조회, 상태가 70이 아니면 진행중으로 간주)
            //거래상태 리턴해주기
            List<Transaction> transactions = transactionRepositories.finds(rentalIdx, findUser.getUserIdx(), false, new Integer[]{70});

            if (transactions.size() != 0) {
                for (int i = 0; i < transactions.size(); i++) {
                    if (transactions.get(i).getUser().getUserIdx() == findChat.getFromUser().getUserIdx()) {
                        int ownerStatus = transactions.get(i).getOwnerStatus();
                        data.addProperty("transactionIdx", transactions.get(i).getTransactionIdx());
                        data.addProperty("statusKey", ownerStatus);
                    } else {
                        //조회된 거래정보에 있는 렌탈러 정보와, 채팅룸에 저장된 렌탈러 정보가 다르면
                        //신규거래 취급
                        data.addProperty("statusKey", 10);
                    }
                }
            } else {
                //신규거래 취급
                data.addProperty("statusKey", 10);
            }

        } else if (findChat == null){
            //찾아진 채팅이 없다는거는 나가기를 했거나 처음인거라 렌탈중일 수 없기 때문 빈페이지 무방하나
            //방 번호리턴을 위해 내가 나가기 한 방 찾아서 방 번호 리턴

            ChatRoom findRoom = chatRepository.findByRental_rentalIdxAndVisibleToIn(rentalIdx, new Long[]{findUser.getUserIdx()});
            if (findRoom != null) {
                data.addProperty("roomIdx", findRoom.getChatRoomIdx());
                data.addProperty("statusKey", 10);
                data.addProperty("status", "매칭대기");

            } else {
                //내가 나가기한 방까지 없다면 신규방이므로
                //방을 만들고 방번호리턴
                ChatRoom room = new ChatRoom();
                room.setVisibleTo(0L);
                room.setRental(findRental);
                room.setFromUser(findUser);
                room.setToUser(findRental.getUser());
                room.setFirstYn(true);

                ChatRoom newRoom = chatService.createChat(room);
                data.addProperty("roomIdx", newRoom.getChatRoomIdx());
                data.addProperty("statusKey", 10);
                data.addProperty("status", "매칭대기");
                if (findUser.getUserIdx() == room.getToUser().getUserIdx()) {
                    data.addProperty("nickName", room.getFromUser().getNickName());
                } else {
                    data.addProperty("nickName", room.getToUser().getNickName());
                }

            }

        }

        //방번호 리턴

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

        }
}