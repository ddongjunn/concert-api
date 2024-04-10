package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import com.api.concert.infrastructure.queue.QueueEntity;
import jakarta.validation.constraints.Null;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {
    private final long QUEUE_EXPIRED_TIME = 1L;
    private final int QUEUE_LIMIT = 5;

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

    @DisplayName("대기열이 가득 찬 경우 WAIT 상태로 등록")
    @Test
    void test_createQueue(){
        // Given
        Long userId = 1L;

        // When
        Queue queue = Queue.builder().userId(userId).build();
        when(iQueueRepository.getCountOfOngoingStatus()).thenReturn(50L);
        queue.updateStatusForOngoingCount(50L, QUEUE_LIMIT, QUEUE_EXPIRED_TIME);

        // Then
        Queue result = queueService.createQueue(userId);
        assertThat(result.getStatus()).isEqualTo(WaitingStatus.WAIT);
        assertThat(result.getExpiredAt()).isNull();
    }

    @DisplayName("대기열이 가득 차지 않은 경우 ONGOING 상태로 등록")
    @Test
    void test_createQueue2(){
        // Given
        Long userId = 1L;

        // When
        Queue queue = Queue.builder().userId(userId).build();
        when(iQueueRepository.getCountOfOngoingStatus()).thenReturn(3L);
        queue.updateStatusForOngoingCount(3L, QUEUE_LIMIT, QUEUE_EXPIRED_TIME);

        // Then
        Queue result = queueService.createQueue(userId);
        assertThat(result.getStatus()).isEqualTo(WaitingStatus.ONGOING);
        assertThat(result.getExpiredAt()).isNotNull();
        assertThat(result.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS));
    }

    /***
     * 1. 대기열 만료시간이 지난 리스트 ONGOING -> DONE
     * 2. 대기열 만료시간이 지난 리스트 !isEmpty()
     * 3. 대기열 만료시간이 지난 리스트 수 만큼 WAIT 상태 리스트(created_at ASC) WAIT -> ONGOING
     */
    @DisplayName("대기열 상태 갱신 ONGOING -> DONE, WAIT -> ONGOING")
    @Test
    void test_expiredOngoingStatusToDone(){
        // Given
        LocalDateTime currentDateTimeMinusOneMinutes = LocalDateTime.now().minusMinutes(1);
        List<Queue> expiredOngoingStatus = List.of(
                Queue.builder().concertWaitingId(1L).userId(1L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(2L).userId(2L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(3L).userId(3L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(4L).userId(4L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(5L).userId(5L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build()
        );

        List<Queue> queuesInWaitStatus = List.of(
                Queue.builder().concertWaitingId(6L).userId(6L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(7L).userId(7L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(8L).userId(8L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(9L).userId(9L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(10L).userId(10L).status(WaitingStatus.WAIT).build()
        );

        // When
        when(iQueueRepository.getExpiredOngoingStatus()).thenReturn(expiredOngoingStatus);

        queueService.updateStatusToDone(expiredOngoingStatus);
        doNothing().when(iQueueRepository).updateStatusToDone(anyList());

        queueService.updateStatusToOngoingForWaitQueues();
        when(iQueueRepository.getQueuesInWaitStatus(anyInt())).thenReturn(queuesInWaitStatus);
        doNothing().when(iQueueRepository).updateStatusToOngoing(anyList());

        // Then
        queueService.expiredOngoingStatusToDone();
        assertThat(expiredOngoingStatus)
                .extracting(Queue::getStatus)
                .containsOnly(WaitingStatus.DONE);

        assertThat(queuesInWaitStatus)
            .allSatisfy(queue -> {
                assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
                assertThat(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                        .isEqualTo(LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME).truncatedTo(ChronoUnit.SECONDS));
            });
    }

    @DisplayName("상태를 DONE 으로 업데이트한다.")
    @Test
    void test_updateStatusToDone(){
        // Given
        LocalDateTime currentDateTimeMinusOneMinutes = LocalDateTime.now().minusMinutes(1);
        List<Queue> expiredOngoingStatus = List.of(
                Queue.builder().concertWaitingId(1L).userId(1L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(2L).userId(2L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(3L).userId(3L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(4L).userId(4L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build(),
                Queue.builder().concertWaitingId(5L).userId(5L).status(WaitingStatus.ONGOING).expiredAt(currentDateTimeMinusOneMinutes).build()
        );

        // When
        doNothing().when(iQueueRepository).updateStatusToDone(anyList());
        queueService.updateStatusToDone(expiredOngoingStatus);

        // Then
        verify(iQueueRepository).updateStatusToDone(anyList());

        assertThat(expiredOngoingStatus)
                .extracting(Queue::getStatus)
                .containsOnly(WaitingStatus.DONE);
    }

    @DisplayName("대기열 중 WAIT 상태인 경우 ONGOING 상태로 변환한다.")
    @Test
    void test_updateStatusToOngoingForWaitQueues(){
        // Given
        given(iQueueRepository.getCountOfOngoingStatus()).willReturn(2L);
        int availableQueueSpace = queueService.calculateAvailableQueueSpace();
        List<Queue> queuesInWaitStatus = List.of(
                Queue.builder().concertWaitingId(1L).userId(1L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(2L).userId(2L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(3L).userId(3L).status(WaitingStatus.WAIT).build()
        );

        // When
        when(iQueueRepository.getQueuesInWaitStatus(anyInt())).thenReturn(queuesInWaitStatus);
        queuesInWaitStatus.forEach(queue -> queue.updateStatusToOngoingAndExpiredAt(QUEUE_EXPIRED_TIME));
        doNothing().when(iQueueRepository).updateStatusToOngoing(anyList());

        // Then
        queueService.updateStatusToOngoingForWaitQueues();
        verify(iQueueRepository).updateStatusToOngoing(anyList());

        assertThat(queuesInWaitStatus)
                .extracting(Queue::getStatus)
                .containsOnly(WaitingStatus.ONGOING);
    }

    @DisplayName("대기열 자리 계산") // 대기열 제한값 - 현재 ONGOING 상태
    @Test
    void test_calculateAvailableQueueSpace(){
        // Given
        long currentOngoingCount = 3L;

        // When
        when(iQueueRepository.getCountOfOngoingStatus()).thenReturn(currentOngoingCount);

        // Then
        int result = queueService.calculateAvailableQueueSpace();
        assertThat(result).isEqualTo(2);
    }
}