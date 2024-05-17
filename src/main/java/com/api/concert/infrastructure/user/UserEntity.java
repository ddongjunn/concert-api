package com.api.concert.infrastructure.user;

import com.api.concert.modules.jpa.entity.BaseEntity;
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
    private Long userId;

    private Long point;

    @Builder
    public UserEntity(Long userId, Long point){
        this.userId = userId;
        this.point = point;
    }
}
