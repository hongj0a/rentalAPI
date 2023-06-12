package dejay.rnd.billyG.repositoryImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dejay.rnd.billyG.domain.*;
import dejay.rnd.billyG.repository.ChatRoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepositories implements ChatRoomRepositoryCustom {
    @PersistenceContext
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    QChatRoom chatRoom = QChatRoom.chatRoom;


    @Override
    public Page<ChatRoom> findAll(Long fromIdx, Long toIdx, Long[] visibleTo,  Pageable pageable){


        List<ChatRoom> result = queryFactory.select(chatRoom).distinct().from(chatRoom)
                .where(
                        (chatRoom.fromUser.userIdx.eq(fromIdx)
                                .or(chatRoom.toUser.userIdx.eq(toIdx)))
                .and(chatRoom.visibleTo.notIn(visibleTo))
                .and(chatRoom.updator.isNotNull()))
                .orderBy(chatRoom.updateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        List<ChatRoom> content = result.stream().toList();
        return new PageImpl<>(content, pageable, content.size());


    }

    public List<ChatRoom> getTotalCount(Long fromIdx, Long toIdx, Long[] visibleTo) {

        List<ChatRoom> totalInfo = queryFactory.select(chatRoom).distinct().from(chatRoom)
                .where(
                        (chatRoom.fromUser.userIdx.eq(fromIdx)
                                .or(chatRoom.toUser.userIdx.eq(toIdx)))
                                .and(chatRoom.visibleTo.notIn(visibleTo))
                                .and(chatRoom.updator.isNotNull()))
                .orderBy(chatRoom.updateAt.desc())
                .fetch();
        return totalInfo.stream().toList();

    }

    public ChatRoom getChat(Long fromIdx, Long toIdx, Long[] visibleTo, Long rentalIdx) {
        ChatRoom one = queryFactory.select(chatRoom).distinct().from(chatRoom)
                .where(
                        (chatRoom.fromUser.userIdx.eq(fromIdx)
                                .or(chatRoom.toUser.userIdx.eq(toIdx)))
                                .and(chatRoom.visibleTo.notIn(visibleTo))
                                .and(chatRoom.rental.rentalIdx.eq(rentalIdx))
                                .and(chatRoom.updator.isNotNull()))
                .fetchOne();
        return one;
    }

}

