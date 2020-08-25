package ru.centralhardware.telegram.znatokiStudentBot.Web.Dto;

import java.util.Optional;

public record EditForm(
        String name,
        String secondName,
        String lastName,
        int classNumber,
        String address,
        String date_of_record,
        String date_of_birth,
        String telephone,
        String telephone_mother,
        String telephone_father,
        String telephone_grandmother,
        String email,
        String place_of_work_mother,
        String place_of_work_father,
        String mother_name,
        String father_name,
        String grandmother_name,
        String sessionId,
        String diseases,
        Optional<String> chemistry,
        Optional<String> biology,
        Optional<String> german,
        Optional<String> english,
        Optional<String> primary_classes,
        Optional<String> russian,
        Optional<String> mathematics,
        Optional<String> social_studies,
        Optional<String> history,
        Optional<String> geography,
        Optional<String> speech_therapy,
        Optional<String> psychology
) {

    public EditForm {
        chemistry = Optional.empty();
        biology = Optional.empty();
        german = Optional.empty();
        english = Optional.empty();
        primary_classes = Optional.empty();
        russian = Optional.empty();
        mathematics = Optional.empty();
        social_studies = Optional.empty();
        history = Optional.empty();
        geography = Optional.empty();
        speech_therapy = Optional.empty();
        psychology = Optional.empty();
    }

}
