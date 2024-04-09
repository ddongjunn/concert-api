package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.Queue;
import com.api.concert.global.entity.BaseEntity;
import com.api.concert.domain.queue.constant.WaitingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "concert_waiting")
@Entity
public class
QueueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long concertWaitingId;

    Long userId;

    @Enumerated(EnumType.STRING)
    WaitingStatus status;

    @Column(nullable = true)
    LocalDateTime expiredAt;

    @Builder
    public QueueEntity(Long concertWaitingId, Long userId, WaitingStatus status, LocalDateTime expiredAt){
        this.concertWaitingId = concertWaitingId;
        this.userId = userId;
        this.status = status;
        this.expiredAt = expiredAt;
    }

}
