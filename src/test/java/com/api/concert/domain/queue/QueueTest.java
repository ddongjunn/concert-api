package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueueTest {

    private final int QUEUE_LIMIT = 5;
    private final int QUEUE_EXPIRED_TIME = 1;

    @DisplayName("[대기열 상태 변경] - 대기열 순번")
    @Test
    void test_updateWaitingNumber(){
        // Given
        int ranking = 4;
        Queue queue = createTestQueueByStatus(WaitingStatus.WAIT);

        // When
        queue.updateWaitingNumber(ranking);

        // Then
        assertThat(queue.getWaitingNumber()).isEqualTo(ranking);
    }

    @DisplayName("[대기열 상태 변경] - 토큰 만료")
    @Test
    void test_toDone(){
        // Given
        Queue queue = createTestQueueByStatus(WaitingStatus.ONGOING);

        // When
        queue.expiry();

        // Then
        assertThat(queue.getStatus()).isEqualTo(null);
        assertThat(queue.isExpired()).isEqualTo(true);
    }

    @DisplayName("[대기열 상태 변경] wait")
    @Test
    void test_toWait(){
        // Given
        Queue queue = createTestQueueByStatus(null);

        // When
        queue.toWait();

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.WAIT);
    }

    @DisplayName("[대기열 상태 변경] wait -> ongoing")
    @Test
    void test_toOngoing(){
        // Given
        Queue queue = createTestQueueByStatus(WaitingStatus.WAIT);

        // When
        queue.toOngoing(QUEUE_EXPIRED_TIME);

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
        assertThat(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME).truncatedTo(ChronoUnit.SECONDS));
    }

    @DisplayName("[대기열 상태 검증] - 대기열 상태가 WAIT 일 경우 Exception")
    @Test
    void test_assertNotWait(){
        // Given
        Queue queue = createTestQueueByStatus(WaitingStatus.WAIT);

        // When & Then
        assertThatThrownBy(queue::assertNotWait)
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.ALREADY_WAITING_USER)
                .hasMessage(ResponseCode.ALREADY_WAITING_USER.getMessage());
    }

    @DisplayName("[대기열 상태 검증] - 대기열 상태가 ONGOING 일 경우 Exception")
    @Test
    void test_assertNotOngoing(){
        // Given
        Queue queue = createTestQueueByStatus(WaitingStatus.ONGOING);
        String message = String.format("대기열 만료 시간 [%s]", queue.getExpiredAt());

        // When & Then
        assertThatThrownBy(queue::assertNotOngoing)
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.ALREADY_ONGOING_USER)
                .hasMessage(message);
    }


    public Queue createTestQueueByStatus(WaitingStatus status){
        return Queue.builder()
                .queueId(1L)
                .userId(1L)
                .status(status)
                .isExpired(false)
                .build();
    }
}