package com.api.concert.infrastructure.queue;

import com.api.concert.modules.jpa.entity.BaseEntity;
import com.api.concert.domain.queue.constant.WaitingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "queue",
        uniqueConstraints = {
            @UniqueConstraint(name= "userId_status",
            columnNames={"userId", "status"}
            )
        }
    )
@Entity
public class QueueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queueId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private WaitingStatus status;

    @ColumnDefault("false")
    private boolean isExpired;

    @Column(nullable = true)
    private LocalDateTime expiredAt;


    @Builder
    public QueueEntity(Long queueId, Long userId, WaitingStatus status, boolean isExpired, LocalDateTime expiredAt){
        this.queueId = queueId;
        this.userId = userId;
        this.status = status;
        this.isExpired = isExpired;
        this.expiredAt = expiredAt;
    }

}
