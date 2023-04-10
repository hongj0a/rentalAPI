package dejay.rnd.billyG.service;

import dejay.rnd.billyG.repositoryImpl.AlarmRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AlarmService {
    private final AlarmRepositories alarmRepositories;

}
