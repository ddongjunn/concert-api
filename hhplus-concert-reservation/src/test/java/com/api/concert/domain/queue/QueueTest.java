package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class QueueTest {

    private Queue queue;
    private final long QUEUE_EXPIRED_TIME = 10L;
    private final int QUEUE_LIMIT = 50;

    @BeforeEach
    void setUp(){
        this.queue = new Queue();
    }

    @Test
    @DisplayName("현재 대기열이 가득찬 경우 WAIT 상태로 반환")
    void givenOngoingCount_whenQueueIsFull_thenStatusWait(){
        // Given
        long ongoingCount = 50;

        // When
        queue.updateStatusForOngoingCount(ongoingCount, QUEUE_LIMIT, QUEUE_EXPIRED_TIME);

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.WAIT);
    }

    @Test
    @DisplayName("현재 대기열이 가득차지않은 경우 ONGOING 상태로 반환")
    void givenOngoingCount_whenQueueIsNotFull_thenStatusOngoing(){
        // Given
        long ongoingCount = 49;

        // When
        queue.updateStatusForOngoingCount(ongoingCount, QUEUE_LIMIT, QUEUE_EXPIRED_TIME);

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
        assertThat(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME).truncatedTo(ChronoUnit.SECONDS));
    }
}