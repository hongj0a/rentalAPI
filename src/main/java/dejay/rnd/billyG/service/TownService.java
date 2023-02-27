package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repository.TownRepositories;
import dejay.rnd.billyG.repository.TownRepository;
import dejay.rnd.billyG.repository.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TownService {
    private final TownRepositories townRepositories;
    private final TownRepository townRepository;
    private final UserRepositories userRepositories;

    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    public List<Town> findAllN(Long userIdx) {
        return townRepositories.findAllN(userIdx);
    }

    @Transactional
    public void setUserTownInfo(Long userIdx, String town_name, boolean lead) {


        User findUser = userRepositories.findOne(userIdx);

        Town town = new Town();
        town.setCreateAt(now_date);
        town.setLeadTown(lead);
        town.setTownName(town_name);
        town.setUser(findUser);

        townRepository.save(town);
    }

    @Transactional
    public void updateLeadTown(Long townIdx, String leadTownName) {

        Town findTown = townRepository.getOne(townIdx);
        findTown.setTownName(leadTownName);
        findTown.setUpdateAt(now_date);
    }

    public List<Town> findAllList(Long userIdx) {
        return townRepositories.findAllList(userIdx);
    }
}
