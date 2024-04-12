package com.api.concert.infrastructure.user;

import com.api.concert.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    Long point;

    @Builder
    public UserEntity(Long userId, Long point){
        this.userId = userId;
        this.point = point;
    }
}
