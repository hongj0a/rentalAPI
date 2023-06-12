package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repositoryImpl.TownRepositories;
import dejay.rnd.billyG.repository.TownRepository;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import dejay.rnd.billyG.util.FrontUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TownService {
    private final TownRepository townRepository;


    @Transactional
    public Town setTowns(String town_name) {

        if (townRepository.findByTownName(town_name) == null) {
            Town town = new Town();
            town.setCreateAt(FrontUtil.getNowDate());
            town.setUpdateAt(FrontUtil.getNowDate());
            town.setTownName(town_name);


            townRepository.save(town);
        }

        return townRepository.findByTownName(town_name);
    }

    @Transactional
    public void updateLeadTown(Long townIdx, String leadTownName) {

        Town findTown = townRepository.getOne(townIdx);
        findTown.setTownName(leadTownName);
        findTown.setUpdateAt(FrontUtil.getNowDate());
    }


}
