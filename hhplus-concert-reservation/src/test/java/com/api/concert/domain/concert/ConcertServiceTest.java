package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    IConcertRepository iConcertRepository;

    @InjectMocks
    ConcertService concertService;

    @DisplayName("[콘서트 조회]")
    @Test
    void test_findAvailableConcerts() {
        // Given
        ConcertInfo concertOne = mock(ConcertInfo.class);
        ConcertInfo concertTwo = mock(ConcertInfo.class);
        List<ConcertInfo> mockConcerts = Arrays.asList(concertOne, concertTwo);

        // When
        when(iConcertRepository.availableConcerts()).thenReturn(mockConcerts);

        // Then
        List<ConcertInfo> result = concertService.getAvailableConcerts();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        verify(iConcertRepository, times(1)).availableConcerts();
    }

}