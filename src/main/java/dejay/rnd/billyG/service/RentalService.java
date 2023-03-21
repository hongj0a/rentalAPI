package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalService {
    private final UserRepositories userRepositories;
    private final RentalRepositories rentalRepositories;
    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

}
