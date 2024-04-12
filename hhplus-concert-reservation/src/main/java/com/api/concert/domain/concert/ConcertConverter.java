package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertResponseDto;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;

import java.util.List;
import java.util.stream.Collectors;

public class ConcertConverter {
    public static ConcertResponseDto toResponse(List<ConcertInfo> concerts){
        List<ConcertResponseDto.ConcertDto> concertDto = concerts.stream()
                .map(concert -> ConcertResponseDto.ConcertDto.builder()
                        .concertOptionId(concert.getConcertOptionId())
                        .name(concert.getName())
                        .singer(concert.getSinger())
                        .venue(concert.getVenue())
                        .startDate(concert.getStartDate())
                        .build())
                .collect(Collectors.toList());
        return ConcertResponseDto.builder().concerts(concertDto).build();
    }
}
