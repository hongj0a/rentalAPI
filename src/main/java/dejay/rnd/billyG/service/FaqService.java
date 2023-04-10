package dejay.rnd.billyG.service;

import dejay.rnd.billyG.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FaqService {
    private final FaqRepository faqRepository;
    //private final FaqTypeRepository faqTypeRepository;

   /* public List<FaqDto> findByFaqType(Long faqType, Pageable pageable) {
        FaqType id = new FaqType();
        id.setFaqTypeIdx(faqType);
        return faqRepository.findByFaqTypeAndDeleteYnAndActiveYn(id,false,true,pageable).stream().map(FaqDto::new).collect(Collectors.toList());

    }*/

    /*public List<FaqTypeDto> findAll(Pageable pageable) {
        return faqTypeRepository.findByDeleteYn(false,pageable).stream().map(FaqTypeDto::new).collect(Collectors.toList());
    }*/
}
