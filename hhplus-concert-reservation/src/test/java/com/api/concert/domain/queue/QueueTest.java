package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class QueueTest {

    private Queue queue;

    @BeforeEach
    void setUp(){
        this.queue = new Queue();
    }

    @DisplayName("현재 대기열이 50이상인 경우 WAIT 상태")
    @Test
    void givenOngoingCount50Over_when(){
        // Given
        int ongoingCount = 50;

        // When
        queue.updateStatusForOngoingCount(ongoingCount);

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.WAIT);
    }

    @DisplayName("현재 대기열이 50미만인 경우 ONGOING 상태")
    @Test
    void givenOngoingCount50less_when(){
        // Given
        int ongoingCount = 49;

        // When
        queue.updateStatusForOngoingCount(ongoingCount);

        // Then
        assertThat(queue.getStatus()).isEqualTo(WaitingStatus.ONGOING);
        assertThat(queue.getExpiredAt().toLocalDate()).isEqualTo(LocalDateTime.now().plusMinutes(10).toLocalDate());
        assertThat(queue.getExpiredAt().getHour()).isEqualTo(LocalDateTime.now().plusMinutes(10).getHour());
        assertThat(queue.getExpiredAt().getMinute()).isEqualTo(LocalDateTime.now().plusMinutes(10).getMinute());
    }
}