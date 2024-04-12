package com.api.concert.infrastructure.concert.projection;

import java.time.LocalDateTime;

public interface ConcertInfo {
    Long getConcertId();
    String getName();
    String getSinger();
    String getVenue();
    LocalDateTime getStartDate();
}
