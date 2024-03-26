package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {

/*    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //@Query(value = "SELECT a FROM LectureEntity a WHERE a.id = :lectureId")
    Optional<LectureEntity> findById(Long lectureId);*/


   /* @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Modifying
    @Query("UPDATE LectureEntity l SET l.name = :name, l.applicantCount = :applicantCount, " +
            "l.capacityLimit = :capacityLimit, l.startDate = :startDate WHERE l.id = :id")
    void update(@Param("id") Long id,
                @Param("name") String name,
                @Param("applicantCount") int applicantCount,
                @Param("capacityLimit") int capacityLimit,
                @Param("startDate") LocalDateTime startDate);*/
}
