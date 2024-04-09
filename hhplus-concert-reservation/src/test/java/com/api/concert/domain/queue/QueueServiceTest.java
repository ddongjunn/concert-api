package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import com.api.concert.infrastructure.queue.QueueEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    IQueueRepository iQueueRepository;

    @InjectMocks
    QueueService queueService;

    @DisplayName("[대기열 등록]")
    @Test
    void test_register(){
        // Given
        QueueRequest queueRequest = QueueRequest.builder().userId(1L).build();
        Long userId = queueRequest.getUserId();

        // When
        queueService.isUserAlreadyRegistered(userId);
        Queue queue = queueService.createQueue(userId);

        Queue savedQueue = Queue.builder().concertWaitingId(1L).userId(1L).status(WaitingStatus.WAIT).build();
        when(iQueueRepository.save(any(QueueEntity.class))).thenReturn(savedQueue);

        QueueResponse expected = QueueConverter.toResponse(savedQueue);
        QueueResponse result = queueService.register(queueRequest);

        // Then
        assertThat(result.getWaitNumber()).isEqualTo(expected.getWaitNumber());
        assertThat(result.getExpiredAt()).isEqualTo(expected.getExpiredAt());
        assertThat(result.getMessage()).isEqualTo(expected.getMessage());
    }

    @DisplayName("대기열에 이미 존재하는 사용자가 신청하면 Exception")
    @Test
    void test_isUserAlreadyRegistered(){
        // Given
        Long userId = 1L;

        // When
        when(iQueueRepository.existsByUserIdAndStatusIsOngoingOrWaiting(userId)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> queueService.isUserAlreadyRegistered(userId))
                .isInstanceOf(AlreadyWaitingUserException.class);
    }
}