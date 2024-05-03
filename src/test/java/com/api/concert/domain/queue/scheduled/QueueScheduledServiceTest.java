package com.api.concert.domain.queue.scheduled;

import com.api.concert.domain.queue.IQueueRedisRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueueScheduledServiceTest {

    @Mock
    private IQueueRedisRepository iQueueRedisRepository;

    @InjectMocks
    QueueScheduledService queueScheduledService;

}