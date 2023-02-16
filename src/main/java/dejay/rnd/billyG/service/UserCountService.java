package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Grade;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.dto.TokenDto;
import dejay.rnd.billyG.dto.UserDto;
import dejay.rnd.billyG.except.DuplicateMemberException;
import dejay.rnd.billyG.except.NotFoundMemberException;
import dejay.rnd.billyG.jwt.TokenProvider;
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
public class UserCountService {

   private UserCountRepository userCountRepository;

    @Transactional
    public void setUserLoginCount(Long userIdx) {

        //usercount 있으면 업데이트

    }
}
