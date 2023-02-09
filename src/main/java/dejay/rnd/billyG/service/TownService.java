package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.repository.TownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TownService {
    private final TownRepository townRepository;

    public List<Town> findAllN(Long userIdx) {
        return townRepository.findAllN(userIdx);
    }

}
