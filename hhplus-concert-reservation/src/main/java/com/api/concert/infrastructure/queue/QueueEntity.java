package com.api.concert.infrastructure.queue;

import com.api.concert.global.entity.BaseEntity;
import com.api.concert.domain.queue.constant.WaitingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "queue")
@Entity
public class QueueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertWaitingId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private WaitingStatus status;

    @Column(nullable = true)
    private LocalDateTime expiredAt;

    @Builder
    public QueueEntity(Long concertWaitingId, Long userId, WaitingStatus status, LocalDateTime expiredAt){
        this.concertWaitingId = concertWaitingId;
        this.userId = userId;
        this.status = status;
        this.expiredAt = expiredAt;
    }

}
