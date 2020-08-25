package ru.centralhardware.telegram.znatokiStudentBot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.LessonTime;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Subject;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Subject subject;
    @Column(nullable = false)
    @Getter
    @Setter
    private Date date;
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private LessonTime lessonTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    @Getter
    private TelegramUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forPupil")
    @Getter
    private Pupil pupil;
    @Column(name = "processed", columnDefinition = "boolean default false")
    @Getter
    @Setter
    private boolean processed;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    @Getter
    private Date createDate;

    public Lesson(TelegramUser createdBy, Pupil pupil) {
        this.createdBy = createdBy;
        this.pupil = pupil;
    }

    @Override
    public String toString() {
        return "id: " + TelegramUtils.makeBold(id) + "\n" +
                "предмет: " + TelegramUtils.makeBold(subject.getRusName()) + "\n" +
                "дата: " + TelegramUtils.makeBold(new SimpleDateFormat("dd-MM-yyyy").format(date)) + "\n" +
                "начало занятия: " + TelegramUtils.makeBold(lessonTime.getStartTime()) + "\n" +
                "ученик: " + TelegramUtils.makeBold(pupil.getFio()) + "\n" +
                "учитель: " + createdBy;
    }

    public String toStringWithoutMarkdown() {
        return "id: " + id + "\n" +
                "предмет: " + subject.getRusName() + "\n" +
                "дата: " + new SimpleDateFormat("dd-MM-yyyy").format(date) + "\n" +
                "начало занятия: " + lessonTime.getStartTime() + "\n" +
                "ученик: " + pupil.getFio() + "\n" +
                "учитель: " + createdBy;
    }

    public int getLessonTimeIndex() {
        return lessonTime.ordinal();
    }
}
