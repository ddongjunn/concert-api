package com.api.concert.infrastructure.concert.projection;

import java.time.LocalDateTime;

public interface ReservationInfo {
    String getName();
    String getSinger();
    LocalDateTime getStartDate();

}
