package com.hhplus.api.lecture.application.service;

import com.hhplus.api.lecture.adapter.out.persistence.LectureRepository;
import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ApplyLectureServiceConcurrencyTest {

    private final ApplyLectureService applyLectureService;
    private final LectureRepository lectureRepository;

    @Autowired
    public ApplyLectureServiceConcurrencyTest(ApplyLectureService applyLectureService, LectureRepository lectureRepository) {
        this.applyLectureService = applyLectureService;
        this.lectureRepository = lectureRepository;
    }


    @Test
    void 여러_사용자가_동시에_강의를_신청하는_경우() throws InterruptedException {
        // Given
        ApplyLectureCommand command = new ApplyLectureCommand(
                1L,
                1L);

        // When
        int numberOfRequests = 100;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for (int i = 0; i < numberOfRequests; i++) {
            Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    applyLectureService.apply(
                            new ApplyLectureCommand(1L, userId)
                    );
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        long lectureCnt = lectureRepository.countById(1L);
        LectureEntity lectureEntity = lectureRepository.findById(1L).get();
        //List<LectureHistory> lectureHistories = loadLectureHistoryPort.loadByLectureId(1L);
        assertThat(lectureEntity.getApplicantCount()).isEqualTo(30);
        //assertThat(lectureHistories.size()).isEqualTo(30); 통과..
    }
}
