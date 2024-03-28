package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.domain.Lecture;

import java.util.List;

public interface LoadLecturesPort {
    List<Lecture> load();
}
