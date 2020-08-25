package ru.centralhardware.telegram.znatokiStudentBot.Steps;

public enum AddPupilSteps implements Nextable<AddPupilSteps> {

    INPUT_FIO,
    INPUT_CLASS_NUMBER,
    INPUT_ADDRESS,
    INPUT_DATE_OF_RECORD,
    INPUT_DATE_OF_BIRTH,
    INPUT_TELEPHONE,
    INPUT_TELEPHONE_FATHER,
    INPUT_TELEPHONE_MOTHER,
    INPUT_TELEPHONE_GRAND_MOTHER,
    INPUT_EMAIL,
    INPUT_SUBJECT,
    INPUT_HOW_TO_KNOW,
    INPUT_CONGENITAL_DISEASES,
    INPUT_PLACE_OF_WORK_MOTHER,
    INPUT_PLACE_OF_WORK_FATHER,
    INPUT_MOTHER_NAME,
    INPUT_FATHER_NAME,
    INPUT_HOBBIES,
    INPUT_GRAND_MOTHER_NAME;

    private static final AddPupilSteps[] values = values();

    public AddPupilSteps next() {
        return values[(ordinal() + 1) % values.length];
    }

}
