package ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum;

import lombok.Getter;

public enum Subject {

    CHEMISTRY("химия"),
    BIOLOGY("биология"),
    GERMAN("немецкий язык"),
    ENGLISH("английский язык"),
    PRIMARY_CLASSES("начальные классы"),
    RUSSIAN("русский язык"),
    MATHEMATICS("математика"),
    SOCIAL_STUDIES("обществознание"),
    HISTORY("история"),
    GEOGRAPHY("география"),
    SPEECH_THERAPIST("логопед"),
    PSYCHOLOGY("психология"),
    PHYSICS("физика");

    @Getter
    final String rusName;

    Subject(String rusName) {
        this.rusName = rusName;
    }

    public static Subject getConstant(String name) {
        for (Subject subject : values()) {
            if (subject.rusName.equals(name)) return subject;
        }
        return null;
    }


    public static boolean validate(String subject) {
        for (Subject sub : values()) {
            if (sub.rusName.equals(subject)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return rusName;
    }
}
