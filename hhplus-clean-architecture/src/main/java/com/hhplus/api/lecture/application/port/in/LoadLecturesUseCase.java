package com.hhplus.api.lecture.application.port.in;

import com.hhplus.api.lecture.domain.Lecture;

import java.util.List;

public interface LoadLecturesUseCase {
    List<Lecture> load();
}
