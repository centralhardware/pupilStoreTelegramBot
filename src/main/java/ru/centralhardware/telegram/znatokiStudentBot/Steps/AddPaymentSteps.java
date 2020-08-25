package ru.centralhardware.telegram.znatokiStudentBot.Steps;

public enum AddPaymentSteps implements Nextable<AddPaymentSteps> {

    INPUT_SUBJECT,
    INPUT_HOW_TO_PAY,
    INPUT_DATE_OF_PAY,
    INPUT_AMOUNT;

    private static final AddPaymentSteps[] values = values();

    public AddPaymentSteps next() {
        return values[(ordinal() + 1) % values.length];
    }

}
