package com.api.concert.domain.concert;

import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    IConcertOptionRepository iConcertOptionRepository;

    @InjectMocks
    ConcertService concertService;

    @DisplayName("예약 가능한 콘서트 조회")
    @Test
    void test_findAvailableConcerts() {
        // Given
        List<ConcertInfo> mockConcerts = makeReservedConcerts(5);

        // When
        when(iConcertOptionRepository.availableConcerts()).thenReturn(mockConcerts);

        // Then
        List<ConcertInfo> result = concertService.findAvailableConcerts();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(5);
        verify(iConcertOptionRepository, times(1)).availableConcerts();
    }

    @DisplayName("예약 가능한 콘서트가 없는 경우")
    @Test
    void test_findAvailableConcerts_notExist() {
        // Given
        List<ConcertInfo> mockConcerts = Collections.emptyList();

        // When & Then
        when(iConcertOptionRepository.availableConcerts()).thenReturn(mockConcerts);

        assertThatThrownBy(() -> concertService.findAvailableConcerts())
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.NO_CONCERT_AVAILABLE)
                .hasMessage(ResponseCode.NO_CONCERT_AVAILABLE.getMessage());
    }

    List<ConcertInfo> makeReservedConcerts(int size){
        return IntStream.rangeClosed(1, size)
                .mapToObj(i -> mock(ConcertInfo.class))
                .toList();
    }

}