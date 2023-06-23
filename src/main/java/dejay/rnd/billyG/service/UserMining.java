package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.UserRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class UserMining {
    private final UserRepository userRepository;
    private final KmsService kmsService;
    public UserMining(UserRepository userRepository, KmsService kmsService) {
        this.userRepository = userRepository;
        this.kmsService = kmsService;
        ;
    }


    public User getUserInfo(String refreshToken) throws ParseException {

        //tokenFlag true 일 때
        //refreshToken decode --> payload json parsing
        //userId 추출해서 회원정보 검색
        //refresh Token 검사해서 update.
        String[] chunks = refreshToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        JSONParser parser = new JSONParser();
        Object obj = parser.parse( payload );
        JSONObject jsonObj = (JSONObject) obj;

        String email = (String) jsonObj.get("sub");
        User findUser = userRepository.findByEmail(email);

        return findUser;
    }
}
