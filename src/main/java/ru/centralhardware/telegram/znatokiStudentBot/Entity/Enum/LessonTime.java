package ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum;

import lombok.Getter;

public enum LessonTime {
    FIRST("c 7 по 8", "07:00", "08:00"),
    SECOND("c 8 по 9", "08:00", "09:00"),
    THIRD("c 9 по 10", "10:00", "10:00"),
    FOURTH("c 10 по 11", "10:00", "11:00"),
    FIFTH("c 11 по 12", "11:00", "12:00"),
    SIXTH("c 12 по 13", "12:00", "13:00"),
    SEVENTH("c 13 по 14", "13:00", "14:00"),
    EIGHT("c 14 по 15", "14:00", "15:00"),
    NINTH("c 15 по 16", "15:00", "16:00"),
    TENTH("c 16 по 17", "16:00", "17:00"),
    ELEVENTH("c 17 по 18", "17:00", "18:00"),
    TWELFTH("c 18 по 19", "18:00", "19:00"),
    THIRTEENTH("c 19 по 20", "19:00", "20:00"),
    FOURTEENTH("c 20 по 21", "20:00", "21:00");

    @Getter
    final String rusName;
    @Getter
    final String startTime;
    @Getter
    final String endTime;

    LessonTime(String rusName, String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.rusName = rusName;
    }

    public static LessonTime getConstant(String name) {
        for (LessonTime lessonTime : values()) {
            if (lessonTime.rusName.equals(name)) return lessonTime;
        }
        return null;
    }

    public static LessonTime getConstByStartTime(String startTime) {
        for (LessonTime lessonTime : values()) {
            if (lessonTime.startTime.equals(startTime)) return lessonTime;
        }
        return null;
    }


    public static boolean validate(String subject) {
        for (LessonTime lessonTime : values()) {
            if (lessonTime.rusName.equals(subject)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return rusName;
    }

}
