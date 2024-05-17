package com.api.concert.infrastructure.concert.projection;

import java.time.LocalDateTime;

public interface ReservationInfoProjection {
    Long getUserId();
    String getName();
    String getSinger();
    int getSeatNo();
    int getPrice();
    LocalDateTime getStartDate();

}
