package com.hhplus.api.lecture.application.service;

import com.hhplus.api.lecture.adapter.out.persistence.LectureHistoryRepository;
import com.hhplus.api.lecture.adapter.out.persistence.LectureRepository;
import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureHistoryEntity;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ApplyLectureServiceConcurrencyTest {

    private final ApplyLectureService applyLectureService;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Autowired
    public ApplyLectureServiceConcurrencyTest(ApplyLectureService applyLectureService, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.applyLectureService = applyLectureService;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }


    @Test
    void 여러_사용자가_강의를_신청하는_경우() throws InterruptedException {
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
        LectureEntity lectureEntity = lectureRepository.findById(1L).get();
        List<LectureHistoryEntity> lectureHistories = lectureHistoryRepository.findByLectureId(1L);
        assertThat(lectureEntity.getApplicantCount()).isEqualTo(30);
        assertThat(lectureHistories.size()).isEqualTo(30);
    }

    @Test
    void 한명의_사용자가_동일한_강의를_신청하는_경우() throws InterruptedException {
        // Given
        ApplyLectureCommand command = new ApplyLectureCommand(
                1L,
                1L);

        // When
        int numberOfRequests = 100;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    applyLectureService.apply(
                            new ApplyLectureCommand(1L, 1L)
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
        int applicantCount = lectureRepository.findById(1L).get().getApplicantCount();
        List<LectureHistoryEntity> lectureHistories = lectureHistoryRepository.findByLectureId(1L);
        assertThat(applicantCount).isEqualTo(1);
        assertThat(lectureHistories.size()).isEqualTo(1);
    }
}
