package com.api.concert.domain.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    IConcertRepository iConcertRepository;

    @InjectMocks
    ConcertService concertService;

    @DisplayName("[콘서트 조회]")
    @Test
    void test_findAvailableConcerts(){
        // Given

        // When
        //when(iConcertRepository.findAvailableConcerts()).thenReturn(anyList());

        // Then
        List<Concert> availableConcerts = concertService.findAvailableConcerts();
        assertThat(availableConcerts).isEmpty();
    }
}