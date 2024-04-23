package com.api.concert.infrastructure.point;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "point_history")
public class PointHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    Long point;

    @Enumerated(value = EnumType.STRING)
    TransactionType type;
}
