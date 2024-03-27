package com.hhplus.api.lecture.application.service;

import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.WriteLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@TestPropertySource(locations = "classpath:application.yml")
public class ApplyLectureServiceDbTest {

    private final LoadLecturePort loadLecturePort;
    private final ModifyLecturePort modifyLecturePort;
    private final LoadLectureHistoryPort loadLectureHistoryPort;
    private final WriteLectureHistoryPort writeLectureHistoryPort;


    private final ApplyLectureService applyLectureService;
    @Autowired
    public ApplyLectureServiceDbTest(LoadLecturePort loadLecturePort,
                                     ModifyLecturePort modifyLecturePort,
                                     WriteLectureHistoryPort writeLectureHistoryPort,
                                     LoadLectureHistoryPort loadLectureHistoryPort) {

        this.loadLecturePort = loadLecturePort;
        this.modifyLecturePort = modifyLecturePort;
        this.writeLectureHistoryPort = writeLectureHistoryPort;
        this.loadLectureHistoryPort = loadLectureHistoryPort;


        this.applyLectureService = new ApplyLectureService(loadLecturePort, modifyLecturePort, loadLectureHistoryPort, writeLectureHistoryPort);
    }

    @Test
    void db_동시성_테스트() throws InterruptedException {
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
        Lecture lecture = loadLecturePort.loadById(1L);
        List<LectureHistory> lectureHistories = loadLectureHistoryPort.loadByLectureId(1L);
        assertThat(lecture.getApplicantCount()).isEqualTo(30);
        //assertThat(lectureHistories.size()).isEqualTo(30); 통과..
    }
}
