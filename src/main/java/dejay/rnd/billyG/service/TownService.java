package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Town;
import dejay.rnd.billyG.domain.User;
import dejay.rnd.billyG.repositoryImpl.TownRepositories;
import dejay.rnd.billyG.repository.TownRepository;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
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
    private final TownRepositories townRepositories;
    private final TownRepository townRepository;
    private final UserRepositories userRepositories;

    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);


   /* public List<Town> findTownInfo(User user) {
        return townRepositories.findTownInfo(user);
    }*/


    @Transactional
    public void setUserTownInfo(Long userIdx, String town_name, boolean lead) {


        User findUser = userRepositories.findOne(userIdx);

        Town town = new Town();
        town.setCreateAt(now_date);
        town.setTownName(town_name);

        townRepository.save(town);
    }

    @Transactional
    public Town setTowns(String town_name) {

        if (townRepository.findByTownName(town_name) == null) {
            Town town = new Town();
            town.setCreateAt(now_date);
            town.setUpdateAt(now_date);
            town.setTownName(town_name);

            townRepository.save(town);
        }

        return townRepository.findByTownName(town_name);
    }

    @Transactional
    public void updateLeadTown(Long townIdx, String leadTownName) {

        Town findTown = townRepository.getOne(townIdx);
        findTown.setTownName(leadTownName);
        findTown.setUpdateAt(now_date);
    }


    /*@Transactional
    public void insertTestTownInfo() {

        for (int i=1; i < 10001; i++) {

            TestTownInfo ti = new TestTownInfo();

            if (i % 2000 == 0) {
                ti.setTestRentalIdx(2000);
            } else {
                ti.setTestRentalIdx(i % 2000);
            }

            if ((i < 2001) && (i % 2 == 0)) {
                ti.setTownIdx(1);
            } else if ((i < 4001) && (i % 2 == 0)) {
                ti.setTownIdx(2);
            } else if ((i < 6001) && (i % 2 == 0)) {
                ti.setTownIdx(3);
            } else if ((i < 8001) && (i % 2 == 0)) {
                ti.setTownIdx(4);
            } else if ((i < 10001) && (i % 2 == 0)) {
                ti.setTownIdx(5);
            } else {
                ti.setTownIdx((int) (Math.random() * 13) + 12);
            }

            ti.setActiveYn(true);
            ti.setCreateAt(now_date);

            townRepositories.save(ti);
        }

    }*/
}
