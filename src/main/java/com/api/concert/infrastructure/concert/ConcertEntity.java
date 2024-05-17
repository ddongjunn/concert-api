package com.api.concert.infrastructure.concert;

import com.api.concert.modules.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "concert")
public class ConcertEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertId;

    private String name;

    private String singer;

    @Builder
    public ConcertEntity(Long concertId, String name, String singer) {
        this.concertId = concertId;
        this.name = name;
        this.singer = singer;
    }
}
