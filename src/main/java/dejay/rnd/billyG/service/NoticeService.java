package dejay.rnd.billyG.service;

import dejay.rnd.billyG.dto.NoticeDto;
import dejay.rnd.billyG.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeDto> noticeList(Pageable pageable)
    {
        return noticeRepository.findByDeleteYn(false, pageable).stream().map(NoticeDto::new).collect(Collectors.toList());
    }
}
