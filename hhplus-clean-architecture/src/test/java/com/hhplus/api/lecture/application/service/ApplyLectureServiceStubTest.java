package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.out.ApplyLecturePort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApplyLectureServiceStubTest {

    private static final Map<Long, Lecture> lectures = new HashMap<>();

    static class TestLoadLecturePort implements LoadLecturePort{

        @Override
        public Lecture loadById(Long lectureId) {
            return lectures.get(lectureId);
        }

    }

    static class TestModifyLecturePort implements ModifyLecturePort{
        @Override
        public void modify(Lecture lecture) {
            lectures.put(lecture.getId(), lectures.getOrDefault(1L, Lecture.of(1L, "k8s", 0, 20, LocalDateTime.now())));
        }
    }
    @BeforeEach
    void setUp(){
        lectures.put(1L, Lecture.of(1L, "k8s",0, 20, LocalDateTime.now()));
    }

    @Mock
    private final ApplyLecturePort applyLecturePort =
            Mockito.mock(ApplyLecturePort.class);

    TestLoadLecturePort testLoadLecturePort = new TestLoadLecturePort();

    TestModifyLecturePort testModifyLecturePort = new TestModifyLecturePort();

    private final ApplyLectureService applyLectureService =
            new ApplyLectureService(testLoadLecturePort, applyLecturePort, testModifyLecturePort);

    @Test
    void stub_동시성_테스트() throws InterruptedException {

        // Given
        ApplyLectureCommand command = new ApplyLectureCommand(
                1L,
                1L);

        // When
        int numberOfRequests = 50;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    applyLectureService.apply(command);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        assertThat(lectures.get(1L).getApplicantCount()).isEqualTo(20);
    }

    @Test
    void stub_수강_신청_테스트(){
        ApplyLectureCommand command = new ApplyLectureCommand(
                1L,
                1L);

        ResponseMessage apply = applyLectureService.apply(command);

        assertThat(apply.code()).isEqualTo("성공");
        assertThat(lectures.get(1L).getApplicantCount()).isEqualTo(1);
    }
}
