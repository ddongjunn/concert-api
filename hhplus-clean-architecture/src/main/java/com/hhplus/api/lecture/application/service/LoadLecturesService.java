package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.lecture.application.port.in.LoadLecturesUseCase;
import com.hhplus.api.lecture.application.port.out.LoadLecturesPort;
import com.hhplus.api.lecture.domain.Lecture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadLecturesService implements LoadLecturesUseCase {

    private final LoadLecturesPort loadLecturesPort;
    @Override
    public List<Lecture> load() {
        return loadLecturesPort.load();
    }

}
