package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.common.exception.CommonException;
import com.api.concert.common.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    IQueueRedisRepository iQueueRedisRepository;

    @InjectMocks
    QueueService queueService;

    @Test
    @DisplayName("[대기열 등록] - 순번(rank)을 rank + 1 반환")
    void test_register(){
        // Given
        QueueRegisterRequest queueRegisterRequest = createTestQueueRegisterRequestByUserId(1L);

        // When
        when(iQueueRedisRepository.register(anyLong())).thenReturn(1);

        // Then
        QueueRegisterResponse register = queueService.register(queueRegisterRequest);
        assertThat(register.getRank()).isEqualTo(2);
    }

    @Test
    @DisplayName("[대기열 조회] - ongoingQueue에 존재하지 않는 경우 Exception")
    void test_getQueueStatus(){
        // Given
        Long userId = 1L;

        // When
        when(iQueueRedisRepository.findUserExpiredTimeInOngoingQueue(userId))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> queueService.getQueueStatus(userId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.NOT_EXIST_WAITING_USER)
                .hasMessage(ResponseCode.NOT_EXIST_WAITING_USER.getMessage());
    }

    @Test
    @DisplayName("[대기열 조회] - ongoingQueue에 존재하는 경우 대기열 만료 시간 반환")
    void test_getQueueStatus1(){
        // Given
        Long userId = 1L;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expiryTime = LocalDateTime.now().format(dateTimeFormatter);

        // When
        when(iQueueRedisRepository.findUserExpiredTimeInOngoingQueue(userId))
                .thenReturn(Optional.of(expiryTime));

        // Then
        QueueStatusResponse result = queueService.getQueueStatus(1L);
        assertThat(result.getExpiredTime()).isEqualTo(expiryTime);
        assertThat(result.getRank()).isNull();
    }

    @Test
    @DisplayName("[대기열 조회] - waitingQueue에 등록되어 있는 경우 대기열 순번 반환")
    void test_getQueueStatus2(){
        // Given
        Long userId = 1L;

        // When
        when(iQueueRedisRepository.findUserRankInWaitQueue(userId))
                .thenReturn(Optional.of(0));

        // Then
        QueueStatusResponse result = queueService.getQueueStatus(1L);
        assertThat(result.getRank()).isEqualTo(1);
        assertThat(result.getExpiredTime()).isNull();
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
        for (int i = 1; i <= size; i++) {
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