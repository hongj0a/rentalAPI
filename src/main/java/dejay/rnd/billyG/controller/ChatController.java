package dejay.rnd.billyG.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dejay.rnd.billyG.api.RestApiRes;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.dto.ChatContentDto;
import dejay.rnd.billyG.dto.ChatRoomDto;
import dejay.rnd.billyG.except.AppException;
import dejay.rnd.billyG.except.ErrCode;
import dejay.rnd.billyG.repository.*;
import dejay.rnd.billyG.service.ChatService;
import dejay.rnd.billyG.util.UserMiningUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final RentalImageRepository rentalImageRepository;
    private final RentalRepository rentalRepository;
    private final ChatContentRepository chatContentRepository;
    private final SimpMessagingTemplate template;
    private final TransactionRepository transactionRepository;


    public ChatController(ChatService chatService, UserRepository userRepository, ChatRepository chatRepository, RentalImageRepository rentalImageRepository, RentalRepository rentalRepository, ChatContentRepository chatContentRepository, SimpMessagingTemplate template, TransactionRepository transactionRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.rentalRepository = rentalRepository;
        this.chatContentRepository = chatContentRepository;
        this.template = template;
        this.transactionRepository = transactionRepository;
    }

    @MessageMapping("/hello")
    @SendTo("/room/chats")
    public ChatContent chatting(@DestinationVariable Long roomIdx, ChatContent message) {


        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomIdx(roomIdx);


        chatService.createChat(chatRoom);
        return ChatContent.builder()
                .user(chatRoom.getRental().getUser())
                .content(message.getContent())
                .build();
    }

    @MessageMapping("/message")
    public void receiveMessage(@Payload ChatRoomDto chatRoomDto) {
        //TODO -- 방 만들기
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatContentDto contentDto, HttpServletRequest req) throws ParseException {
        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        ChatRoom findRoom = chatRepository.getOne(contentDto.getChatRoomIdx());

        ChatContent chat = new ChatContent();
        chat.setUser(findUser);
        chat.setChatRoom(findRoom);
        chat.setContent(contentDto.getMessage());
        chatService.insert(chat);

        findRoom.setLastChatMessage(contentDto.getMessage());
        findRoom.setUpdator(findUser.getEmail());

        chatService.updateChatRoom(findRoom);
        template.convertAndSend("/sub/chat/room" + contentDto.getChatRoomIdx(), contentDto);
    }

    @GetMapping("/getChatRooms")
    public ResponseEntity<JsonObject> getChatRooms(Pageable pageable, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();
        JsonArray roomArr = new JsonArray();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        Page<ChatRoom> rooms = chatRepository.findByFromUser_userIdxOrToUser_userIdxAndVisibleToNotInOrUpdatorIsNotNullOrderByUpdateAtDesc(findUser.getUserIdx(), findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()}, pageable);
        List<ChatRoom> totalRooms = chatRepository.findByFromUser_userIdxOrToUser_userIdxAndVisibleToNotInOrUpdatorIsNotNull(findUser.getUserIdx(), findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()});

        rooms.forEach(
                rm -> {
                    JsonObject rms = new JsonObject();
                    rms.addProperty("roomIdx", rm.getChatRoomIdx());
                    if (rm.getToUser().getUserIdx() == findUser.getUserIdx()) {
                        rms.addProperty("userSeq", rm.getFromUser().getUserIdx());
                        rms.addProperty("image", rm.getFromUser().getProfileImageUrl());
                        rms.addProperty("nickName", rm.getFromUser().getNickName());
                    } else if (rm.getFromUser().getUserIdx() == findUser.getUserIdx()) {
                        rms.addProperty("userSeq", rm.getToUser().getUserIdx());
                        rms.addProperty("image", rm.getToUser().getProfileImageUrl());
                        rms.addProperty("nickName", rm.getToUser().getNickName());
                    }

                    rms.addProperty("lastMessage", rm.getLastChatMessage());
                    //readYn
                    /*if (rm.getUpdator().equals(findUser.getEmail())) {
                        rms.addProperty("readYn",true);
                    } else if (!rm.getUpdator().equals(findUser.getEmail()) ){
                        // 마지막메시지 내가 아닌경우 false, new 달고 상세갈때 read_yn 업뎃 돼야함//컬럼추가
                        rms.addProperty("readYn", rm.isReadYn());
                    }*/
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

    @GetMapping("/isRoomExistCheck")
    public ResponseEntity<JsonObject> isRoomExistCheck(@RequestParam(value="rentalIdx") Long rentalIdx, Pageable pageable, HttpServletRequest req) throws AppException, ParseException {
        JsonObject data = new JsonObject();

        String acToken = req.getHeader("Authorization").substring(7);
        String userEmail = UserMiningUtil.getUserInfo(acToken);
        User findUser = userRepository.findByEmail(userEmail);

        RestApiRes<JsonObject> apiRes = new RestApiRes<>(data, req);

        Rental findRental = rentalRepository.getOne(rentalIdx);

        if (findUser.getUserIdx() != findRental.getUser().getUserIdx()) {
            ChatRoom findChat = chatRepository.findByFromUser_userIdxAndVisibleToNotInAndRental_rentalIdxAndUpdatorIsNotNull(findUser.getUserIdx(), new Long[]{-1L, findUser.getUserIdx()}, rentalIdx);

            if (findChat != null) {
                //나가기 이후 보여주기
                Page<ChatContent> chats = chatContentRepository.findByChatRoom_chatRoomIdx(findChat.getChatRoomIdx(), pageable);
                List<ChatContent> size = chatContentRepository.findByChatRoom_chatRoomIdx(findChat.getChatRoomIdx());
                //채팅목록 뿌려주기
                chats.forEach(
                        ch -> {
                            JsonObject chs = new JsonObject();

                            chs.addProperty("messageIdx", ch.getChatIdx());
                            if (findUser.getUserIdx() != ch.getUser().getUserIdx()) {
                                chs.addProperty("image", ch.getUser().getProfileImageUrl());
                            }
                            chs.addProperty("message", ch.getContent());
                            chs.addProperty("regDate", ch.getCreateAt().getTime());
                        }
                );

                data.addProperty("totalCount", size.size());
                //거래이력 조회하기 (게시글사용자, 로그인사용자, 렌탈인덱스로 조회되는 트랜잭션있는지 조회, 상태가 70이 아니면 진행중으로 간주)
                //거래상태 리턴해주기

                List<Transaction> transactions = transactionRepository.findByRental_rentalIdxAndUser_userIdxAndOwnerStatusNotIn(rentalIdx, findUser.getUserIdx(), new int[]{70});

                if (transactions.size() != 0) {
                    //거래중인거는 무조건 1개일 수 밖에 없음
                    int ownerStatus = transactions.get(0).getOwnerStatus();
                    int renterStatus = transactions.get(0).getRenterStatus();
                    int returnStatus = 0;
                    String status;

                    returnStatus = ownerStatus > renterStatus ? ownerStatus : renterStatus;

                    switch (returnStatus) {
                        case 20:
                            status = "매칭완료";
                            break;
                        case 30:
                            status = "렌탈중";
                            break;
                        case 40:
                            status = "물품인수";
                            break;
                        case 50:
                            status = "물품반납";
                            break;
                        case 60:
                            status = "이의신청";
                            break;
                        case 70:
                            status = "렌탈완료";
                            break;
                        default:
                            status = "매칭대기";
                            break;

                    }

                    data.addProperty("statusKey", returnStatus);
                    data.addProperty("status", status);

                } else {
                    //신규채팅 취급
                    data.addProperty("statusKey", 10);
                    data.addProperty("status", "매칭대기");

                }
            } else {
                //찾아진 채팅이 없다는거는 나가기를 했거나 처음인거라 렌탈중일 수 없기 때문 빈페이지 무방하나
                //방 번호리턴을 위해 내가 나가기 한 방 찾아서 방 번호 리턴

                ChatRoom findRoom = chatRepository.findByRental_rentalIdxAndVisibleToIn(rentalIdx, new Long[]{findUser.getUserIdx()});
                if (findRoom != null) {
                    data.addProperty("roomId", findRoom.getChatRoomIdx());
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

                    ChatRoom newRoom = chatService.createChat(room);
                    data.addProperty("roomId", newRoom.getChatRoomIdx());
                    data.addProperty("statusKey", 10);
                    data.addProperty("status", "매칭대기");
                }

            }
        } else {
            //잘못된 접근 내가 나에게 말 걸 수 없음
            apiRes.setError(ErrCode.err_param_authentication.code());
            apiRes.setMessage(ErrCode.err_param_authentication.msg());

            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());
        }

            //방번호 리턴

            return new ResponseEntity<>(RestApiRes.data(apiRes), new HttpHeaders(), apiRes.getHttpStatus());

        }
}