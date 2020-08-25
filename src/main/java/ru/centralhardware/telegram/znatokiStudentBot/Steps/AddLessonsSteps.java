package ru.centralhardware.telegram.znatokiStudentBot.Steps;

public enum AddLessonsSteps implements Nextable<AddLessonsSteps> {
    INPUT_SUBJECT,
    INPUT_DATE,
    INPUT_TIME;

    private static final AddLessonsSteps[] values = values();

    public AddLessonsSteps next() {
        return values[(ordinal() + 1) % values.length];
    }

}
