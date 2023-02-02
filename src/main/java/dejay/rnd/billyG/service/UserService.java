package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public User signup(LoginDto loginDto) {

        LocalDateTime date = LocalDateTime.now();
        Date reg_date = Timestamp.valueOf(date);

        User user = new User();

        user.setIdEmail(loginDto.getIdEmail());
        user.setEmail(loginDto.getIdEmail());
        user.setSnsType(loginDto.getSnsType());
        user.setCreateAt(reg_date);
        user.setLevelupAt(reg_date);
        user.setLastLoginDate(reg_date);

        //token insert

        System.out.println("UserService.signup");
        return userRepository.save(user);
    }


}
