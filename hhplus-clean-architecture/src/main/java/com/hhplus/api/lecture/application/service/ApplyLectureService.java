package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.annotation.RedissonLock;
import com.hhplus.api.common.exception.CustomException;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureUseCase;
import com.hhplus.api.lecture.application.port.out.ApplyLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyLectureService implements ApplyLectureUseCase {

    private final LoadLecturePort loadLecturePort;
    private final ModifyLecturePort modifyLecturePort;
    private final ApplyLectureHistoryPort applyLectureHistoryPort;
    private final RedissonClient redissonClient;

    @Transactional
    public ResponseMessage apply(ApplyLectureCommand command) {
        RLock lock = redissonClient.getLock(command.getLectureId().toString());
        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                log.error("lock fail()");
                return new ResponseMessage("fail", "신청 실패");
            }

            boolean isApplicationExists = applyLectureHistoryPort.isApplicationExists(command.getLectureId(), command.getUserId());
            if(isApplicationExists){
                return new ResponseMessage("fail", "이미 신청한 강의");
            }

            Lecture lecture = loadLecturePort.loadById(command.getLectureId());
            if (!lecture.isApplicationPossible()) {
                return new ResponseMessage("에러", "정원 초과");
            }

            lecture.incrementApplicantCount();
            modifyLecturePort.modify(lecture);

            applyLectureHistoryPort.save(
                    LectureHistory.of(lecture.getId(),
                            command.getUserId(),
                            LocalDateTime.now()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return new ResponseMessage("성공", "신청 완료");
    }

}
