package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.domain.UserCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCountRepository extends JpaRepository<UserCount, Long> {

    UserCount findByUser_UserIdx(Long userIdx);
}
