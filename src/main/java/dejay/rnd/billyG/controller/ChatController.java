package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.ChatContentDto;
import dejay.rnd.billyG.dto.ChatRoomDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.model.ImageFile;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.repositoryImpl.ChatRepositories;
import dejay.rnd.billyG.repositoryImpl.TransactionRepositories;
import dejay.rnd.billyG.service.ChatService;
import dejay.rnd.billyG.service.FileUploadService;
import dejay.rnd.billyG.service.TransactionService;
import dejay.rnd.billyG.util.UserMiningUtil;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    private final ChatContentRepository chatContentRepository;
    private final SimpMessagingTemplate template;
    private final FileUploadService uploadService;
    private final TransactionRepository transactionRepository;
    private final ChatImageRepository chatImageRepository;
    private final ChatRepositories chatRepositories;
    private final TransactionService transactionService;
    private final TransactionRepositories transactionRepositories;


    public ChatController(ChatService chatService, UserRepository userRepository, ChatRepository chatRepository, RentalImageRepository rentalImageRepository, RentalRepository rentalRepository, ChatContentRepository chatContentRepository, SimpMessagingTemplate template, FileUploadService uploadService, TransactionRepository transactionRepository, ChatImageRepository chatImageRepository, ChatRepositories chatRepositories, TransactionService transactionService, TransactionRepositories transactionRepositories) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalRepository = rentalRepository;
        this.chatContentRepository = chatContentRepository;
        this.template = template;
        this.uploadService = uploadService;
        this.transactionRepository = transactionRepository;
        this.chatImageRepository = chatImageRepository;
        this.chatRepositories = chatRepositories;
        this.transactionService = transactionService;
        this.transactionRepositories = transactionRepositories;
    }

    @Transactional(rollbackFor = Exception.class)
    @MessageMapping(value = "/chat/message")
    public void message(@Payload ChatContentDto contentDto){
        LocalDateTime date = LocalDateTime.now();
        Date thisDate = Timestamp.valueOf(date);
        long now_date = Timestamp.valueOf(date).getTime();

        contentDto.setStep(contentDto.getStep());

        String status = "";
        TransactionHistory history = new TransactionHistory();

        User findUser = userRepository.findByUserIdx(contentDto.getUserIdx());
        ChatRoom findRoom = chatRepository.findByChatRoomIdx(contentDto.getChatRoomIdx());
        User renter = userRepository.findByUserIdx(findRoom.getFromUser().getUserIdx());
        List<Transaction> trs = transactionRepository.findByCreateAtEquals(thisDate);
        int len = (int) (Math.log10(trs.size()) + 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String parseDate = dateFormat.format(thisDate);

        //system message인 경우
        if (contentDto.isSystemYn() == true) {
            ChatContent chat = new ChatContent();
            chat.setChatRoom(findRoom);
            chat.setUser(findUser);
            chat.setContent(contentDto.getMessage());
            chat.setSystemYn(true);

            chatService.insert(chat);
            contentDto.setSystemYn(true);
            contentDto.setRegDate(now_date);
        } else if (contentDto.isSystemYn() == false){
            //채팅 메시지가 있는경우
            if (findUser != null && contentDto.getMessage() != null) {
                ChatContent chat = new ChatContent();
                chat.setUser(findUser);
                chat.setChatRoom(findRoom);
                chat.setContent(contentDto.getMessage());
                ChatContent chatContent = chatService.insert(chat);

                findRoom.setLastChatMessage(contentDto.getMessage());
                findRoom.setUpdator(findUser.getEmail());
                findRoom.setUpdateAt(thisDate);

                chatService.updateChatRoom(findRoom);

                contentDto.setMessageSeq(chatContent.getChatIdx());
                contentDto.setNickName(findUser.getNickName());
                contentDto.setImage(findUser.getProfileImageUrl());
                contentDto.setRegDate(now_date);
            }
        }


        if (contentDto.getTransactionIdx() != 0 ) {
            //변경되는 내용 다시저장
            Transaction findTr = transactionRepository.findByTransactionIdx(contentDto.getTransactionIdx());

            if (contentDto.getStep()== 0 ) {

                if (contentDto.isSystemYn() == false) {
                    findTr.setCancelYn(true);
                    findTr.setCancelAt(thisDate);
                    findTr.setStatusAt(thisDate);

                    history.setCancelAt(thisDate);
                    history.setStatusAt(thisDate);
                    history.setCancelYn(true);

                    contentDto.setStatus(String.valueOf(10));
                }

            } else if (contentDto.getStep()==1) {

                history.setOwnerStatus(findTr.getOwnerStatus() + 10);
                history.setRenterStatus(findTr.getRenterStatus() + 10);
                history.setUpdateAt(thisDate);
                history.setStatusAt(thisDate);

                findTr.setOwnerStatus(findTr.getOwnerStatus() + 10);
                findTr.setRenterStatus(findTr.getRenterStatus() + 10);
                findTr.setStatusAt(thisDate);
                status = String.valueOf(findTr.getOwnerStatus()+10);
                contentDto.setStatus(status);
            } else if (contentDto.getStep() == 2) {

                history.setOwnerStatus(70);
                history.setRenterStatus(70);
                history.setUpdateAt(thisDate);
                history.setStatusAt(thisDate);

                findTr.setOwnerStatus(70);
                findTr.setRenterStatus(70);
                findTr.setStatusAt(thisDate);

                findRoom.setFirstYn(true);
                chatService.updateChatRoom(findRoom);

                contentDto.setStatus(String.valueOf(10));
            }

            transactionService.updateTransacion(findTr);

            history.setRentalIdx(findTr.getRental().getRentalIdx());
            history.setTransactionIdx(findTr.getTransactionIdx());


        } else if (contentDto.getTransactionIdx() == 0) {
            //null, 새로 만들어야 하는 시점. 히스토리도 똑같이 입력
                if (contentDto.getStep()==1) {

                    Transaction tr = new Transaction();
                    tr.setOwnerStatus(20);
                    tr.setRenterStatus(20);
                    tr.setCancelYn(false);
                    tr.setRental(findRoom.getRental());
                    tr.setUser(renter);

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
                    tr.setStatusAt(thisDate);

                    Transaction transaction = transactionService.insertTransaction(tr);

                    history.setTransactionIdx(transaction.getTransactionIdx());
                    history.setTransactionNum(transaction.getTransactionNum());
                    history.setOwnerStatus(transaction.getOwnerStatus());
                    history.setRenterStatus(transaction.getRenterStatus());
                    history.setCancelYn(false);
                    history.setRentalIdx(findRoom.getRental().getRentalIdx());
                    history.setStatusAt(thisDate);
                    history.setUpdateAt(thisDate);
                    history.setUserIdx(renter.getUserIdx());

                    contentDto.setStatus(String.valueOf(20));
                    contentDto.setTransactionIdx(tr.getTransactionIdx());
            }

        }

        history.setCreateAt(thisDate);
        history.setUpdator(findUser.getEmail());

        //최종 히스토리 set
        transactionService.insertHistory(history);

        template.convertAndSend("/sub/isRoomExistCheck" + contentDto.getChatRoomIdx(), contentDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value ="/api/setChatImage", consumes = {"multipart/form-data"})
    public ResponseEntity<JsonObject> setChatImage(@RequestParam (value = "images", required = false) List<MultipartFile> images,
                                                   @RequestParam (value = "roomIdx") Long roomIdx,
                                                   HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray imgArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        ChatRoom findRoom = chatRepository.findByChatRoomIdx(roomIdx);

        ChatContent content = new ChatContent();
        content.setImageYn(true);
        content.setChatRoom(findRoom);
        content.setUser(findUser);

        ChatContent chat = chatService.insert(content);

        findRoom.setLastChatMessage("이미지를 보냈습니다.");
        chatService.updateChatRoom(findRoom);

        for (int i = 0; i < images.size(); i++) {
            ChatImage ci = new ChatImage();
            JsonObject img = new JsonObject();
            ImageFile file = uploadService.upload(images.get(i));

            ci.setChatContent(chat);
            ci.setImageUrl(file.getFileName());

            ChatImage chatImage = chatImageRepository.save(ci);
            img.addProperty("imageUrl", chatImage.getImageUrl());
            imgArr.add(img);
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
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

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

                    rms.addProperty("readYn", rm.isReadYn());
                    List<RentalImage> img = rentalImageRepository.findByRental_rentalIdx(rm.getRental().getRentalIdx());
                    rms.addProperty("rentalIdx", rm.getRental().getRentalIdx());
                    rms.addProperty("rentalImage", img.get(0).getImageUrl());
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

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        ChatRoom findChat = chatRepository.getOne(chatRoomDto.getChatRoomIdx());

        if (findChat != null) {
            if (findChat.getVisibleTo() != 0) {
                findChat.setVisibleTo(-1L);
            } else if ( findChat.getVisibleTo() == 0){
                findChat.setVisibleTo(findUser.getUserIdx());
            }
            findChat.setUpdator(findUser.getEmail());

            chatService.updateExit(findChat);
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @PostMapping("/delChatRoom")
    public ResponseEntity<JsonObject> delChatRoom(@RequestBody ChatRoomDto chatRoomDto, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        ChatRoom findChat = chatRepository.findByChatRoomIdx(chatRoomDto.getChatRoomIdx());

        if (findChat.getLastChatMessage() == null) {
            chatRepository.delete(findChat);
        }

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);
        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

    }

    @GetMapping("/isRoomExistCheck")
    public ResponseEntity<JsonObject> isRoomExistCheck(@RequestParam(value="rentalIdx") Long rentalIdx,
                                                       @RequestParam(value="roomIdx", required = false) Long roomIdx, Pageable pageable, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray conArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Rental findRental = rentalRepository.getOne(rentalIdx);

        ChatRoom findChat;
        if (roomIdx == 0) {
            findChat = chatRepositories.getChat(findUser.getUserIdx(), findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()}, rentalIdx);
        } else {
            findChat = chatRepository.findByChatRoomIdx(roomIdx);
        }

        if (findChat != null) {

            if (findChat.isFirstYn() == true && (findChat.getRental().getUser().getUserIdx() == findUser.getUserIdx())) {
                data.addProperty("ownerFirstYn", findChat.isFirstYn());

                findChat.setFirstYn(false);
                chatService.updateChatRoom(findChat);
            } else {
                data.addProperty("ownerFirstYn", findChat.isFirstYn());
            }

            //나가기 이후 보여주기
            Page<ChatContent> chats = chatContentRepository.findByChatRoom_chatRoomIdxOrderByCreateAtDesc(findChat.getChatRoomIdx(), pageable);
            List<ChatContent> size = chatContentRepository.findByChatRoom_chatRoomIdx(findChat.getChatRoomIdx());
            //채팅목록 뿌려주기
            chats.forEach(
                    ch -> {

                        JsonObject chs = new JsonObject();

                        chs.addProperty("messageSeq", ch.getChatIdx());
                        chs.addProperty("userIdx", ch.getUser().getUserIdx());
                        if (findUser.getUserIdx() != ch.getUser().getUserIdx()) {
                            chs.addProperty("imageUrl", ch.getUser().getProfileImageUrl());
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
            data.add("contents", conArr);
            data.addProperty("totalCount", size.size());
            data.addProperty("roomIdx", findChat.getChatRoomIdx());
            //거래이력 조회하기 (게시글사용자, 로그인사용자, 렌탈인덱스로 조회되는 트랜잭션있는지 조회, 상태가 70이 아니면 진행중으로 간주)
            //거래상태 리턴해주기
            List<Transaction> transactions = transactionRepositories.finds(rentalIdx, findUser.getUserIdx(), false, new Integer[]{70});

            if (transactions.size() != 0) {
                //거래중인거는 무조건 1개일 수 밖에 없음
                int ownerStatus = transactions.get(0).getOwnerStatus();

                data.addProperty("transactionIdx", transactions.get(0).getTransactionIdx());
                data.addProperty("statusKey", ownerStatus);

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

            }

        }

        //방번호 리턴

        return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

        }
}