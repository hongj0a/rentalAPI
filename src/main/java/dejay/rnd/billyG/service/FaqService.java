package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.FaqType;
import dejay.rnd.billyG.dto.FaqDto;
import dejay.rnd.billyG.dto.FaqTypeDto;
import dejay.rnd.billyG.repository.FaqRepository;
import dejay.rnd.billyG.repository.FaqTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class FaqService {
    private final FaqRepository faqRepository;
    private final FaqTypeRepository faqTypeRepository;

    public List<FaqDto> findByFaqType(Long faqType, Pageable pageable) {
        FaqType id = new FaqType();
        id.setFaqTypeIdx(faqType);
        return faqRepository.findByFaqTypeAndDeleteYnAndActiveYn(id,false,true,pageable).stream().map(FaqDto::new).collect(Collectors.toList());

    }

    public List<FaqTypeDto> findAll(Pageable pageable) {
        return faqTypeRepository.findByDeleteYn(false,pageable).stream().map(FaqTypeDto::new).collect(Collectors.toList());
    }
}
