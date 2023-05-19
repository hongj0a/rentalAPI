package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Category;
import dejay.rnd.billyG.repositoryImpl.CategoryRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepositories categoryRepositories;

    public CategoryService(CategoryRepositories categoryRepositories) {
        this.categoryRepositories = categoryRepositories;
    }


    public List<Category> findAllN() {
        return categoryRepositories.findAllN();
    }

    public List<Category> findBlockTypes(String blockType) {
        return categoryRepositories.findBlockTypes(blockType);
    }

}
