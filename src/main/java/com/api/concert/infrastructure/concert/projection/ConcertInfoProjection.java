package com.api.concert.infrastructure.concert.projection;

import java.time.LocalDateTime;

public interface ConcertInfoProjection {
    Long getConcertOptionId();
    String getName();
    String getSinger();
    String getVenue();
    LocalDateTime getStartDate();
}
