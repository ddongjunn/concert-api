package com.api.concert.domain.point.event;

import com.api.concert.domain.point.IPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PointHistoryLoggedEventHandler {
    private final IPointHistoryRepository iPointHistoryRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(
            classes = PointHistoryLoggedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(PointHistoryLoggedEvent event) {
        iPointHistoryRepository.save(PointHistoryLoggedEvent.toEntity(event));
    }
}
