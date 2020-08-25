package ru.centralhardware.telegram.znatokiStudentBot.Web.Dto;

import ru.centralhardware.telegram.znatokiStudentBot.Entity.Lesson;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public record TimetableDto(
        String time,
        String subject,
        String pupil,
        String teacher,
        String isProcessed,
        String dateOfCreate
) {

    public TimetableDto(Lesson lesson) {
        this(
                lesson.getLessonTime().getStartTime(),
                lesson.getSubject().getRusName(),
                lesson.getPupil().getFio(),
                lesson.getCreatedBy().getFioForEmail(),
                lesson.isProcessed() ? "Да" : "Нет",
                new SimpleDateFormat("H:m dd-MM-yyyy").format(lesson.getCreateDate())
        );
    }

    public static List<TimetableDto> toDto(List<? extends Lesson> lessons){
        return lessons.
                stream().
                map(TimetableDto::new).
                collect(Collectors.toList());
    }
}
