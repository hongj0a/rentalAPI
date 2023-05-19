package dejay.rnd.billyG.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class UserMiningUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private UserMiningUtil() {}

    public static String getUserInfo(String refreshToken) throws ParseException {

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

        return email;
    }
}
