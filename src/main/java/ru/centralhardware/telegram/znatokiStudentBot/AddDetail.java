package ru.centralhardware.telegram.znatokiStudentBot;

import lombok.Getter;
import ru.centralhardware.telegram.znatokiStudentBot.Steps.Nextable;

public class AddDetail<StepEnum extends Nextable<StepEnum>, Details> {

    @Getter
    private final Details details;
    @Getter
    private StepEnum steps;

    public AddDetail(StepEnum steps, Details pupil) {
        this.steps = steps;
        details = pupil;
    }

    public void nextStep() {
        steps = steps.next();
    }


}
