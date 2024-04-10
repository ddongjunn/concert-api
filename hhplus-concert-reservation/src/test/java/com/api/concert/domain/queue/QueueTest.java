package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.api.concert.domain.queue.QueueOption.QUEUE_EXPIRED_TIME;
import static org.assertj.core.api.Assertions.assertThat;

class QueueTest {

    private Queue queue;

    @BeforeEach
    void setUp(){
        this.queue = new Queue();
    }

    @DisplayName("[대기열 상태 변경] status -> done")
    @Test
    void test_toDone(){
        // Given
        Queue queue = Queue.builder().userId(1L).build();

        // When
        queue.toDone();

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.DONE);
    }

    @DisplayName("[대기열 상태 변경] status -> wait")
    @Test
    void test_toWait(){
        // Given
        Queue queue = Queue.builder().userId(1L).build();

        // When
        queue.toWait();

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.WAIT);
    }

    @DisplayName("[대기열 상태 변경] status -> ongoing")
    @Test
    void test_toOngoing(){
        // Given
        Queue queue = Queue.builder().userId(1L).build();

        // When
        queue.toOngoing(QUEUE_EXPIRED_TIME);

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
        assertThat(queue.getExpiredAt().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME).truncatedTo(ChronoUnit.SECONDS));
    }
}