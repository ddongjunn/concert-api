package com.api.concert.domain.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.api.concert.domain.queue.QueueOption.QUEUE_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;

class QueueOptionTest {

    private QueueOption queueOption;

    @BeforeEach
    void setUp(){
        this.queueOption = new QueueOption();
    }


    @DisplayName("[대기열 카운트 초기화]")
    @Test
    void test_initializeQueueCount(){
        // Given
        Long count = 1L;

        // When
        queueOption.initializeQueueCount(count);

        // Then
        assertThat(queueOption.getOngoingStatusCount().get()).isEqualTo(count);
    }

    @DisplayName("[대기열 공간 확인] - 대기열 ongoing 카운트가 대기열 제한인원 보다 많은 경우")
    @Test
    void test_hasAvailableSpaceInOngoingQueue_false(){
        // Given
        Long ongoingCount = (long) (QUEUE_LIMIT + 1);
        queueOption.initializeQueueCount(ongoingCount);

        // When
        boolean result = queueOption.hasAvailableSpaceInOngoingQueue();

        // Then
        assertThat(result).isEqualTo(false);
    }

    @DisplayName("[대기열 공간 확인] - 대기열 ongoing 카운트가 대기열 제한인원 보다 적은 경우")
    @Test
    void test_hasAvailableSpaceInOngoingQueue_true(){
        // Given
        Long ongoingCount = (long) (QUEUE_LIMIT - 1);
        queueOption.initializeQueueCount(ongoingCount);

        // When
        boolean result = queueOption.hasAvailableSpaceInOngoingQueue();

        // Then
        assertThat(result).isEqualTo(true);
        assertThat(queueOption.getOngoingStatusCount().get()).isEqualTo(QUEUE_LIMIT);
    }

    @Test
    void test_calculateAvailableQueueSpace(){
        // Given
        Long ongoingCount = 4L;
        queueOption.initializeQueueCount(ongoingCount);

        // When
        int result = queueOption.calculateAvailableQueueSpace();

        // Then
        assertThat(result).isEqualTo(1);
    }
}