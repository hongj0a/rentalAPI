package dejay.rnd.billyG.service;

import dejay.rnd.billyG.dto.PushDto;
import dejay.rnd.billyG.repositoryImpl.AlarmRepositories;
import dejay.rnd.billyG.util.FrontUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PushService {

    public void sendPush(Long[] hostIdxes, Long userIdx, Long targetIdx, int type, String title, String message) {
        PushDto pushDto = new PushDto();

        pushDto.setHostIdxes(hostIdxes);
        pushDto.setUserIdx(userIdx);
        pushDto.setTargetIdx(targetIdx);
        pushDto.setType(type);
        pushDto.setTitle(title);
        pushDto.setMessage(message);

        FrontUtil.pushRequest(pushDto);
    }
}
