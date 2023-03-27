package dejay.rnd.billyG.service;

import dejay.rnd.billyG.repositoryImpl.MainRepositories;
import org.springframework.stereotype.Service;

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
