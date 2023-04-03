package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Alarm;
import dejay.rnd.billyG.domain.FaqType;
import dejay.rnd.billyG.dto.FaqDto;
import dejay.rnd.billyG.dto.FaqTypeDto;
import dejay.rnd.billyG.repository.FaqRepository;
import dejay.rnd.billyG.repository.FaqTypeRepository;
import dejay.rnd.billyG.repositoryImpl.AlarmRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class AlarmService {
    private final AlarmRepositories alarmRepositories;

}
