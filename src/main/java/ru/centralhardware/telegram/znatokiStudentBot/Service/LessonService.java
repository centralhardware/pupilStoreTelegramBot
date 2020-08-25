package ru.centralhardware.telegram.znatokiStudentBot.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.LessonTime;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Lesson;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.LessonRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Lesson save(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public List<Lesson> findByLessonTimes(LessonTime lessonTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            log.warn("", e);
        }
        List<Lesson> todayLesson = lessonRepository.findByDate(date);
        return todayLesson.
                stream().
                filter(it -> it.getLessonTime().equals(lessonTime)).
                collect(Collectors.toList());
    }

    public Optional<Lesson> findById(int id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> findByDate(Date date) {
        return lessonRepository.
                findByDate(date).
                stream().
                sorted(Comparator.comparingInt(Lesson::getLessonTimeIndex)).
                collect(Collectors.toList());
    }
}
