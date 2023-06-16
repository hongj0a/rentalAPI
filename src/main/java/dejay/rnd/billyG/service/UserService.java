package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Grade;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.domain.UserCount;
import dejay.rnd.billyG.dto.TokenDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.NotFoundMemberException;
import dejay.rnd.billyG.jwt.TokenProvider;
import dejay.rnd.billyG.repository.GradeRepository;
import dejay.rnd.billyG.repositoryImpl.UserCountRepositories;
import dejay.rnd.billyG.repository.UserCountRepository;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import dejay.rnd.billyG.repository.UserRepository;
import dejay.rnd.billyG.util.FrontUtil;
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
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final UserRepositories userRepositories;
    private final UserCountRepository userCountRepository;
    private final UserCountRepositories userCountRepositories;
    private final GradeRepository gradeRepository;
    private final UserCountService userCountService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, UserRepositories userRepositories, UserCountRepository userCountRepository, UserCountRepositories userCountRepositories, GradeRepository gradeRepository, UserCountService userCountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = tokenProvider;
        this.userRepositories = userRepositories;
        this.userCountRepository = userCountRepository;
        this.userCountRepositories = userCountRepositories;
        this.gradeRepository = gradeRepository;
        this.userCountService = userCountService;
    }

    @Transactional
    public UserDto signup(UserDto userDto) {
        Grade findGrade = gradeRepository.findByGradeScoreLessThanEqual(30L);

        Grade grade = Grade.builder()
                .gradeIdx(Long.valueOf(findGrade.getMenuNum()))
                .build();

        User user = User.builder()
                .email(userDto.getEmail())
                .idEmail(userDto.getEmail())
                .snsType(passwordEncoder.encode(userDto.getSnsType()))
                .snsName(userDto.getSnsType())
                .name(userDto.getName())
                .activityScore(0)
                .starPoint("0")
                .userLevel(Long.valueOf(findGrade.getMenuNum()))
                .status(10)
                .phoneNum(userDto.getPhoneNumber())
                .ciValue(userDto.getCiValue())
                .grades(Collections.singleton(grade))
                .levelupAt(FrontUtil.getNowDate())
                .lastLoginDate(FrontUtil.getNowDate())
                .activeAt(FrontUtil.getNowDate())
                .build();
        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public void updateUserInfo(Long userIdx, String snsType) {


        User findUser = userRepositories.findOne(userIdx);
        findUser.setSnsName(snsType);
        findUser.setSnsType(passwordEncoder.encode(snsType));
        findUser.setUpdateAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void updateUserTownInfo(Map<Integer, Long> userTowns, User findUser) {

        if (userTowns.size() != 0) {
            for (Map.Entry<Integer, Long> pair : userTowns.entrySet()) {
                switch (pair.getKey()) {
                    case 0 :
                        findUser.setLeadTown(pair.getValue());
                        break;
                    case 1 :
                        findUser.setTown1(pair.getValue());
                        break;
                    case 2 :
                        findUser.setTown2(pair.getValue());
                        break;
                    case 3 :
                        findUser.setTown3(pair.getValue());
                        break;
                    case 4 :
                        findUser.setTown4(pair.getValue());
                        break;
                    default :
                        System.out.println("4이상은 안 됨");
                        throw new ArrayIndexOutOfBoundsException();
                }
            }
        }
    }

    @Transactional
    public TokenDto login(String memberId, String password) {
        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);


        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByㅎUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        System.out.println("UserService.login@@@@");
        TokenDto tokenInfo = tokenProvider.createToken(authentication);

        //last_login_date update
        User findUser = userRepository.findByEmail(memberId);
        findUser.setRefreshToken(tokenInfo.getRefreshToken());
        findUser.setLastLoginDate(now_date);
        updateUser(findUser);

        //login_count_update
        //usercount에 있는지 조회, 있으면 update, 없으면 insert if문으로
        UserCount userCount = userCountRepository.findByUser_UserIdx(findUser.getUserIdx());
        if (userCount != null) {
            userCount.setLoginCnt(userCount.getLoginCnt()+1);
            userCountService.updateCnt(userCount);

        } else {
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
    public void setRefreshToken(User user) {
        user.setUpdateAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void setUserProfile(Long userIdx, String nickname, String image_name) {

        User findUser = userRepositories.findOne(userIdx);
        findUser.setProfileImageUrl(image_name);
        findUser.setNickName(nickname);
        findUser.setUpdateAt(FrontUtil.getNowDate());
    }
    @Transactional
    public void setUserProfileImageUrl(Long userIdx, String image_name) {

        User findUser = userRepositories.findOne(userIdx);
        findUser.setProfileImageUrl(image_name);
        findUser.setUpdateAt(FrontUtil.getNowDate());
    }

    @Transactional
    public void setUserNickName(Long userIdx, String nickname) {

        User findUser = userRepositories.findOne(userIdx);
        findUser.setNickName(nickname);
        findUser.setUpdateAt(FrontUtil.getNowDate());
    }
    @Transactional
    public void updateUser(User user) {
        user.setUpdateAt(FrontUtil.getNowDate());
        user.setUpdator(user.getEmail());
    }

    @Transactional
    public void updateCiValue(String phoneNum, User user) {

        user.setPhoneNum(phoneNum);
        user.setUpdateAt(FrontUtil.getNowDate());
    }


}
