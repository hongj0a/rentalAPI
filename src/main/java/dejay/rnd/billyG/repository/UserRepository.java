package dejay.rnd.billyG.repository;

import dejay.rnd.billyG.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "grades")
    Optional<User> findOneWithGradesByEmail(String username);

    User findByEmail(String email);

    User findByCiValue(String ciValue);

    List<User> findAllByNickNameContainingAndDeleteYnAndActiveYn(String nickName, boolean deleteYn, boolean activeYn);
}
