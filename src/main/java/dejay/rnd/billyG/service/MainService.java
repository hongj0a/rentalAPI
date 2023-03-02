package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainService {

    private final MainRepositories mainRepositories;

    public MainService(MainRepositories mainRepositories) {
        this.mainRepositories = mainRepositories;
    }

   /* public List<Rental> findMainList() {
        return mainRepositories.findMainList();
    }*/
}
