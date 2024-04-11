package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.queue.QueueEntity;
import com.api.concert.infrastructure.queue.projection.WaitingRank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.api.concert.domain.queue.QueueOption.QUEUE_EXPIRED_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceUnitTest {
    @Mock
    IQueueRepository iQueueRepository;

    @Mock
    QueueOption queueOption;

    @Mock
    WaitingRank waitingRank;

    @InjectMocks
    QueueService queueService;


    @DisplayName("[대기열 등록]")
    @Test
    void test_register(){
        // Given
        QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId(1L).build();
        Long userId = queueRegisterRequest.getUserId();
        Queue queue = Queue.builder().userId(userId).build();

        // When
        queueService.isUserAlreadyRegistered(userId);
        queueService.assignQueueStatus(queue);

        Queue savedQueue = Queue.builder().concertWaitingId(1L).userId(1L).status(WaitingStatus.ONGOING).build();
        when(iQueueRepository.save(any(QueueEntity.class))).thenReturn(savedQueue);

        QueueRegisterResponse expected = QueueConverter.toRegisterResponse(savedQueue);
        QueueRegisterResponse result = queueService.register(queueRegisterRequest);

        // Then
        assertThat(result.getWaitNumber()).isEqualTo(expected.getWaitNumber());
        assertThat(result.getExpiredAt()).isEqualTo(expected.getExpiredAt());
        assertThat(result.getMessage()).isEqualTo(expected.getMessage());
    }

    @DisplayName("[대기열 중복 신청] 대기열에 이미 존재하는 사용자가 신청하는 경우 Exception")
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


    /***
     * 1. 대기열 만료시간이 지난 리스트 ONGOING -> DONE
     * 2. 대기열 만료시간이 지난 리스트 !isEmpty()
     * 3. 대기열 만료시간이 지난 리스트 수 만큼 WAIT 상태 리스트(created_at ASC) WAIT -> ONGOING
     */
    @DisplayName("[대기열 상태 갱신] ONGOING -> DONE, WAIT -> ONGOING")
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

    @DisplayName("[대기열 만료된 토큰 갱신] ONGOING -> DONE")
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

    @DisplayName("[대기열 토큰 갱신] WAIT -> ONGOING")
    @Test
    void test_updateStatusToOngoingForWaitQueues(){
        // Given
        int availableQueueSpace = 3;
        given(queueOption.calculateAvailableQueueSpace()).willReturn(availableQueueSpace);

        List<Queue> queuesInWaitStatus = List.of(
                Queue.builder().concertWaitingId(1L).userId(1L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(2L).userId(2L).status(WaitingStatus.WAIT).build(),
                Queue.builder().concertWaitingId(3L).userId(3L).status(WaitingStatus.WAIT).build()
        );

        // When
        when(iQueueRepository.getQueuesInWaitStatus(availableQueueSpace)).thenReturn(queuesInWaitStatus);
        doNothing().when(iQueueRepository).updateStatusToOngoing(anyList());

        // Then
        queueService.updateStatusToOngoingForWaitQueues();
        verify(iQueueRepository).updateStatusToOngoing(anyList());

        assertThat(queuesInWaitStatus)
                .extracting(Queue::getStatus)
                .containsOnly(WaitingStatus.ONGOING);
    }

    @DisplayName("[대기열 상태 조회] - 존재하지 않는 번호표")
    @Test
    void test_detail_notExist(){
        // Given
        Long concertWaitingId = 1L;

        // When
        when(iQueueRepository.findById(anyLong())).thenThrow(new CommonException(ResponseCode.TICKET_NOT_ISSUED, ResponseCode.TICKET_NOT_ISSUED.getMessage()));

        // Then
        assertThatThrownBy(() -> queueService.detail(concertWaitingId))
                .isInstanceOf(CommonException.class)
                .hasMessage(ResponseCode.TICKET_NOT_ISSUED.getMessage());
    }

    @DisplayName("[대기열 상태 조회] - WAIT")
    @Test
    void test_detail_statusWait(){
        // Given
        Long concertWaitingId = 1L;
        Queue queue = Queue.builder().concertWaitingId(1L).waitingNumber(1).status(WaitingStatus.WAIT).build();

        // When
        when(iQueueRepository.findById(anyLong())).thenReturn(queue);
        when(waitingRank.getRanking()).thenReturn(1);
        when(iQueueRepository.countWaitingAhead(concertWaitingId)).thenReturn(waitingRank);

        // Then
        QueueStatusResponse result = queueService.detail(concertWaitingId);
        assertThat(result.getWaitNumber()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(WaitingStatus.WAIT);
        assertThat(result.getMessage()).isEqualTo("현재 고객님 순번 : %s", queue.getWaitingNumber());
    }

    @DisplayName("[대기열 상태 조회] - ONGOING")
    @Test
    void test_detail_statusOngoing(){
        // Given
        Long concertWaitingId = 1L;
        Queue queue = Queue.builder().concertWaitingId(1L).expiredAt(LocalDateTime.now()).status(WaitingStatus.ONGOING).build();
        String message = String.format("대기열 만료 시간 [%s]", queue.getExpiredAt());

        // When
        when(iQueueRepository.findById(anyLong())).thenReturn(queue);

        // Then
        assertThatThrownBy(() -> queueService.detail(concertWaitingId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.ALREADY_ONGOING_USER)
                .hasMessage(message);
    }

}