package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceUnitTest {

    private final int QUEUE_LIMIT = 5;
    private final int QUEUE_EXPIRED_TIME = 1;
    @Mock
    IQueueRepository iQueueRepository;

    @Mock
    WaitingRank waitingRank;

    @InjectMocks
    QueueService queueService;


    @DisplayName("[대기열 신청] - 대기열이 가득 차지 않은 경우 ONGOING")
    @Test
    void test_register_ongoing(){
        // Given
        QueueRegisterRequest queueRegisterRequest = createTestQueueRegisterRequestByUserId(1L);
        Long userId = queueRegisterRequest.getUserId();
        Queue queue = Queue.builder().userId(userId).build();

        // When
        when(iQueueRepository.findByUserIdAndStatusIn(anyLong(), anyList())).thenReturn(null);
        queueService.validateUserNotRegisteredOrThrow(userId);

        List<Queue> ongoingStatus = getOngoingQueueListBySize(QUEUE_LIMIT - 1);
        when(iQueueRepository.findByStatusWithPessimisticLock(any(WaitingStatus.class))).thenReturn(ongoingStatus);
        queueService.assignQueueStatusByAvailability(queue);

        Queue savedQueue = Queue.builder().queueId(6L).userId(6L).status(WaitingStatus.ONGOING).build();
        when(iQueueRepository.save(any(QueueEntity.class))).thenReturn(savedQueue);

        QueueRegisterResponse expected = QueueConverter.toRegisterResponse(savedQueue);
        QueueRegisterResponse result = queueService.register(queueRegisterRequest);

        // Then
        assertThat(result.getWaitNumber()).isEqualTo(expected.getWaitNumber());
        assertThat(result.getExpiredAt()).isEqualTo(expected.getExpiredAt());
        assertThat(result.getMessage()).isEqualTo(expected.getMessage());
    }

    @DisplayName("[대기열 신청] - 대기열이 가득 찬 경우 WAIT")
    @Test
    void test_register_wait(){
        // Given
        QueueRegisterRequest queueRegisterRequest = createTestQueueRegisterRequestByUserId(1L);
        Long userId = queueRegisterRequest.getUserId();
        Queue queue = Queue.builder().userId(userId).build();

        // When
        when(iQueueRepository.findByUserIdAndStatusIn(anyLong(), anyList())).thenReturn(null);
        queueService.validateUserNotRegisteredOrThrow(userId);

        List<Queue> ongoingStatus = getOngoingQueueListBySize(QUEUE_LIMIT);
        when(iQueueRepository.findByStatusWithPessimisticLock(any(WaitingStatus.class))).thenReturn(ongoingStatus);
        queueService.assignQueueStatusByAvailability(queue);

        Queue savedQueue = Queue.builder().queueId(6L).userId(6L).status(WaitingStatus.WAIT).build();
        when(iQueueRepository.save(any(QueueEntity.class))).thenReturn(savedQueue);

        QueueRegisterResponse expected = QueueConverter.toRegisterResponse(savedQueue);
        QueueRegisterResponse result = queueService.register(queueRegisterRequest);

        // Then
        assertThat(result.getWaitNumber()).isEqualTo(expected.getWaitNumber());
        assertThat(result.getExpiredAt()).isEqualTo(expected.getExpiredAt());
        assertThat(result.getMessage()).isEqualTo(expected.getMessage());
    }

    @DisplayName("[대기열 중복 신청] - 대기열에 이미 등록된 경우 WAIT")
    @Test
    void test_register_alreadyWait(){
        // Given
        QueueRegisterRequest queueRegisterRequest = createTestQueueRegisterRequestByUserId(1L);
        Long userId = queueRegisterRequest.getUserId();
        Queue queue = createTestQueueByUserIdAndStatus(1L ,WaitingStatus.WAIT);

        // When & Then
        when(iQueueRepository.findByUserIdAndStatusIn(anyLong(), anyList())).thenReturn(queue);
        assertThatThrownBy(() -> queueService.validateUserNotRegisteredOrThrow(userId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.ALREADY_WAITING_USER)
                .hasMessage(ResponseCode.ALREADY_WAITING_USER.getMessage());

    }

    @DisplayName("[대기열 중복 신청] - 대기열에 이미 등록된 경우 ONGOING")
    @Test
    void test_register_alreadyOngoing(){
        // Given
        QueueRegisterRequest queueRegisterRequest = createTestQueueRegisterRequestByUserId(1L);
        Long userId = queueRegisterRequest.getUserId();
        Queue queue = createTestQueueByUserIdAndStatus(1L ,WaitingStatus.ONGOING);
        String message = String.format("대기열 만료 시간 [%s]", queue.getExpiredAt());

        // When & Then
        when(iQueueRepository.findByUserIdAndStatusIn(anyLong(), anyList())).thenReturn(queue);
        assertThatThrownBy(() -> queueService.validateUserNotRegisteredOrThrow(userId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.ALREADY_ONGOING_USER)
                .hasMessage(message);

    }


    /**
     * 1. 대기열 만료시간이 지난 리스트 status update : ONGOING -> null, isExpired -> true
     * 3. 대기열 만료시간이 지난 리스트 수 만큼 WAIT 상태 리스트(created_at ASC) WAIT -> ONGOING
     */
    @DisplayName("[대기열 상태 갱신] 만료된 대기열 리스트 개수 만큼 WAIT 상태 대기열 활성화")
    @Test
    void test_updateExpiredQueuesAndActivateNewOnes(){
        // Given
        LocalDateTime currentDateTimeMinusOneMinutes = LocalDateTime.now().minusMinutes(1);
        List<Queue> expiredQueues = createOngoingListBySizeAndExpiredAt(5, currentDateTimeMinusOneMinutes);
        List<Queue> waitQueues = createWaitListBySize(5);

        // When
        when(iQueueRepository.findExpiredOngoingStatus(any(WaitingStatus.class), any(LocalDateTime.class))).thenReturn(expiredQueues);

        queueService.expireQueues(expiredQueues);
        doNothing().when(iQueueRepository).updateStatusFromWaitToOngoingOrExpired(anyList());

        queueService.activateQueuesByExpireQueuesSize(expiredQueues.size());
        when(iQueueRepository.findQueuesInWaitStatus(any(WaitingStatus.class), anyInt())).thenReturn(waitQueues);
        doNothing().when(iQueueRepository).updateStatusFromWaitToOngoingOrExpired(anyList());

        // Then
        queueService.updateExpiredQueuesAndActivateNewOnes();
        assertThat(expiredQueues)
                .allSatisfy(queue -> {
                    assertThat(queue.getStatus()).isNull();
                    assertThat(queue.isExpired()).isEqualTo(true);
                });

        assertThat(waitQueues)
            .allSatisfy(queue -> {
                assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
                assertThat(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                        .isEqualTo(LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME).truncatedTo(ChronoUnit.SECONDS));
            });
    }

    @DisplayName("[대기열 만료된 토큰 상태값 변경] ONGOING -> DONE")
    @Test
    void test_expireQueues(){
        // Given
        LocalDateTime currentDateTimeMinusOneMinutes = LocalDateTime.now().minusMinutes(1);
        List<Queue> expiredQueues = createOngoingListBySizeAndExpiredAt(5, currentDateTimeMinusOneMinutes);

        // When
        doNothing().when(iQueueRepository).updateStatusFromWaitToOngoingOrExpired(anyList());
        queueService.expireQueues(expiredQueues);

        // Then
        verify(iQueueRepository).updateStatusFromWaitToOngoingOrExpired(anyList());
        assertThat(expiredQueues)
                .allSatisfy(queue -> {
                    assertThat(queue.getStatus()).isNull();
                    assertThat(queue.isExpired()).isEqualTo(true);
                });
    }

    @DisplayName("[대기열 대기중인 토큰 갱신] WAIT -> ONGOING")
    @Test
    void test_activateQueuesByExpireQueuesSize(){
        // Given
        int availableQueueSpace = 3;
        List<Queue> waitQueues = createWaitListBySize(availableQueueSpace);

        // When
        when(iQueueRepository.findQueuesInWaitStatus(any(WaitingStatus.class), anyInt())).thenReturn(waitQueues);
        doNothing().when(iQueueRepository).updateStatusFromWaitToOngoingOrExpired(anyList());

        // Then
        queueService.activateQueuesByExpireQueuesSize(availableQueueSpace);
        verify(iQueueRepository).updateStatusFromWaitToOngoingOrExpired(anyList());
        assertThat(waitQueues)
                .allSatisfy(queue -> {
                    assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
                    assertThat(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                            .isEqualTo(LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME).truncatedTo(ChronoUnit.SECONDS));
                });
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
        Queue queue = Queue.builder().queueId(1L).waitingNumber(1).status(WaitingStatus.WAIT).build();

        // When
        when(iQueueRepository.findById(anyLong())).thenReturn(queue);
        when(waitingRank.getRanking()).thenReturn(1);
        when(iQueueRepository.countWaitingAhead(concertWaitingId)).thenReturn(waitingRank);

        // Then
        QueueStatusResponse result = queueService.detail(concertWaitingId);
        assertThat(result.getWaitNumber()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(WaitingStatus.WAIT);
        assertThat(result.getMessage()).isEqualTo("대기열 순번 : %s", queue.getWaitingNumber());
    }

    @DisplayName("[대기열 상태 조회] - ONGOING")
    @Test
    void test_detail_statusOngoing(){
        // Given
        Long concertWaitingId = 1L;
        Queue queue = Queue.builder().queueId(1L).expiredAt(LocalDateTime.now()).status(WaitingStatus.ONGOING).build();
        String message = String.format("대기열 만료 시간 [%s]", queue.getExpiredAt());

        // When
        when(iQueueRepository.findById(anyLong())).thenReturn(queue);

        // Then
        assertThatThrownBy(() -> queueService.detail(concertWaitingId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.ALREADY_ONGOING_USER)
                .hasMessage(message);
    }

    public QueueRegisterRequest createTestQueueRegisterRequestByUserId(Long userId){
        return QueueRegisterRequest
                .builder()
                .userId(userId)
                .build();
    }
    public Queue createTestQueueByUserIdAndStatus(Long userId, WaitingStatus status){
        return Queue.builder()
                .queueId(1L)
                .userId(userId)
                .status(status)
                .isExpired(false)
                .build();
    }
    public List<Queue> getOngoingQueueListBySize(int size) {
        List<Queue> ongoingStatus = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            ongoingStatus.add(createTestQueueByUserIdAndStatus((long) i, WaitingStatus.ONGOING));
        }
        return ongoingStatus;
    }

    public List<Queue> createOngoingListBySizeAndExpiredAt(int size, LocalDateTime time) {
        List<Queue> ongoingStatus = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            ongoingStatus.add(
                    Queue.builder()
                            .queueId((long) i)
                            .userId((long) i)
                            .status(WaitingStatus.ONGOING)
                            .expiredAt(time)
                            .build()
            );
        }
        return ongoingStatus;
    }

    public List<Queue> createWaitListBySize(int size) {
        List<Queue> ongoingStatus = new ArrayList<>();
        for(int i = 1; i <= size; i++) {
            ongoingStatus.add(
                    Queue.builder()
                            .queueId((long) i + 10)
                            .userId((long) i + 10)
                            .status(WaitingStatus.WAIT)
                            .build()
            );
        }
        return ongoingStatus;
    }

}