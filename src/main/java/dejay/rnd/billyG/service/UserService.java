package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Grade;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.domain.UserCount;
import dejay.rnd.billyG.dto.TokenDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.DuplicateMemberException;
import dejay.rnd.billyG.except.NotFoundMemberException;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.repository.UserCountRepositories;
import dejay.rnd.billyG.repository.UserCountRepository;
import dejay.rnd.billyG.repository.UserRepositories;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.util.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final UserRepositories userRepositories;
    private final UserCountRepository userCountRepository;
    private final UserCountRepositories userCountRepositories;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, UserRepositories userRepositories, UserCountRepository userCountRepository, UserCountRepositories userCountRepositories) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = tokenProvider;
        this.userRepositories = userRepositories;
        this.userCountRepository = userCountRepository;
        this.userCountRepositories = userCountRepositories;
    }

    @Transactional
    public UserDto signup(UserDto userDto) {
        if (userRepository.findOneWithGradesByEmail(userDto.getEmail()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        Grade grade = Grade.builder()
                .gradeName("도톨씨앗")
                .build();

        User user = User.builder()
                .email(userDto.getEmail())
                .snsType(passwordEncoder.encode(userDto.getSnsType()))
                .snsName(userDto.getSnsType())
                .grades(Collections.singleton(grade))
                .activeYn(true)
                .createAt(now_date)
                .build();
        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public TokenDto login(String memberId, String password) {
        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);


        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenInfo = tokenProvider.createToken(authentication);

        //last_login_date update
        User findUser = userRepository.findByEmail(memberId);
        findUser.setLastLoginDate(now_date);

        //login_count_update
        //usercount에 있는지 조회, 있으면 update, 없으면 insert if문으로
        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());
        if (userCount != null) {
            System.out.println("userCount.getCountIdx() = " + userCount.getCountIdx());
            userCount.setLoginCnt(userCount.getLoginCnt()+1);

        } else {
            System.out.println("UserService.login");
            UserCount newCount = new UserCount();
            newCount.setUser(findUser);
            newCount.setLoginCnt(1L);
            newCount.setCreateAt(now_date);

            userCountRepositories.save(newCount);
        }


        return tokenInfo;
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithGrades(String username) {
        return UserDto.from(userRepository.findOneWithGradesByEmail(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithGrades() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithGradesByEmail)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }

    public List<User> findById(String email) {
        return userRepositories.findById(email);
    }

    public List<User> findByNickName(String nickName) { return userRepositories.findByNickName(nickName); }

    @Transactional
    public void setRefreshToken(Long userIdx, String refreshToken) {

        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        User findUser = userRepositories.findOne(userIdx);
        findUser.setRefreshToken(refreshToken);
        findUser.setUpdateAt(now_date);
        findUser.setIdEmail(findUser.getEmail());
    }

    @Transactional
    public void setUserProfile(Long userIdx, String nickname, String image_name) {

        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        User findUser = userRepositories.findOne(userIdx);
        findUser.setProfileImageUrl(image_name);
        findUser.setNickName(nickname);
        findUser.setUpdateAt(now_date);
    }


    @Transactional
    public void updateCiValue(String ciValue, String name, Long userIdx) {

        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        User findUser = userRepositories.findOne(userIdx);
        findUser.setCiValue(ciValue);
        findUser.setName(name);
        findUser.setUpdateAt(now_date);
    }

}
