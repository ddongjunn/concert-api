package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {

    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT a FROM LectureEntity a WHERE a.id = :lectureId")
    Optional<LectureEntity> selectById(Long lectureId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Modifying
    @Query("UPDATE LectureEntity l SET l.name = :name, l.applicantCount = :applicantCount, " +
            "l.capacityLimit = :capacityLimit, l.startDate = :startDate WHERE l.id = :id")
    void update(@Param("id") Long id,
                @Param("name") String name,
                @Param("applicantCount") int applicantCount,
                @Param("capacityLimit") int capacityLimit,
                @Param("startDate") LocalDateTime startDate);*/
}
