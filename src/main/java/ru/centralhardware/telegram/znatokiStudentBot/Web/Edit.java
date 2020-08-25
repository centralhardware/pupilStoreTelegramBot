package ru.centralhardware.telegram.znatokiStudentBot.Web;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.CongenitalDiseases;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Subject;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Session;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PupilService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.SessionService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.DateUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelephoneUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Web.Dto.EditForm;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class Edit {

    private final static Logger log = LoggerFactory.getLogger(Edit.class);
    private final ResourceBundle resourceBundle;
    public static final String ERROR_TITLE = Payment.ERROR_TITLE;
    public static final String ERROR_MESSAGE = Payment.ERROR_MESSAGE;
    public static final String ERROR_PAGE_NAME = "error";
    public static final String BRONCHIAL_ASTHMA = "bronchialAsthma";
    public static final String VEGETATIVE_VASCULAR_DYSTONIA = "vegetativeVascularDystonia";
    public static final String CEREBRAL_PALSY = "cerebralPalsy";
    public static final String DEVELOPMENT_DELAY = "developmentDelay";
    public static final String SECOND_NAME = "secondName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";

    private final SessionService sessionService;
    private final PupilService pupilService;

    public Edit(ResourceBundle resourceBundle, SessionService sessionService, PupilService pupilService) {
        this.resourceBundle = resourceBundle;
        this.sessionService = sessionService;
        this.pupilService   = pupilService;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(@RequestParam String sessionId, Model model) {
        Optional<Session> s = sessionService.findByUuid(sessionId);
        if (s.isPresent()) {
            if (s.get().isExpire()) {
                model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_EXPIRE"));
                model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("SESSION_EXPIRE_GIVE_NEW"));
                return ERROR_PAGE_NAME;
            }
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_NOT_FOUND"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("SESSION_NOT_FOUND"));
            return ERROR_PAGE_NAME;
        }
        Optional<Session> sessionOptional = sessionService.findByUuid(sessionId);
        if (sessionOptional.isPresent()) {
            Pupil pupil = sessionOptional.get().getPupil();
            model.addAttribute("name", pupil.getName());
            model.addAttribute(SECOND_NAME, pupil.getSecondName());
            model.addAttribute(LAST_NAME, pupil.getLastName());
            model.addAttribute("classNumber", pupil.getClassNumber());
            model.addAttribute("address", pupil.getAddress());
            model.addAttribute("date_of_record", DateUtils.dateFormat.format(pupil.getDateOfRecord()));
            model.addAttribute("date_of_birth", DateUtils.dateFormat.format(pupil.getDateOfBirth()));
            model.addAttribute(PupilService.TELEPHONE, pupil.getTelephone());
            model.addAttribute("telephone_mother", pupil.getTelephoneMother());
            model.addAttribute("telephone_father", pupil.getTelephoneFather());
            model.addAttribute("telephone_grandmother", pupil.getTelephoneGrandMother());
            model.addAttribute(EMAIL, pupil.getEmail());
            model.addAttribute("place_of_work_mother", pupil.getPlaceOfWorkMother());
            model.addAttribute("place_of_work_father", pupil.getPlaceOfWorkFather());
            model.addAttribute("mother_name", pupil.getMotherName());
            model.addAttribute("father_name", pupil.getFatherName());
            model.addAttribute("grandmother_name", pupil.getGrandMotherName());
            model.addAttribute("chemistry", pupil.getSubjects().contains(Subject.CHEMISTRY));
            model.addAttribute("biology", pupil.getSubjects().contains(Subject.BIOLOGY));
            model.addAttribute("german", pupil.getSubjects().contains(Subject.GERMAN));
            model.addAttribute("english", pupil.getSubjects().contains(Subject.ENGLISH));
            model.addAttribute("primary_classes", pupil.getSubjects().contains(Subject.PRIMARY_CLASSES));
            model.addAttribute("russian", pupil.getSubjects().contains(Subject.RUSSIAN));
            model.addAttribute("mathematics", pupil.getSubjects().contains(Subject.MATHEMATICS));
            model.addAttribute("social_studies", pupil.getSubjects().contains(Subject.SOCIAL_STUDIES));
            model.addAttribute("history", pupil.getSubjects().contains(Subject.HISTORY));
            model.addAttribute("geography", pupil.getSubjects().contains(Subject.GEOGRAPHY));
            model.addAttribute("speech_therapy", pupil.getSubjects().contains(Subject.SPEECH_THERAPIST));
            model.addAttribute("psychology", pupil.getSubjects().contains(Subject.PSYCHOLOGY));
            model.addAttribute("none", pupil.getCongenitalDiseases() == CongenitalDiseases.NONE);
            model.addAttribute(BRONCHIAL_ASTHMA, pupil.getCongenitalDiseases() == CongenitalDiseases.BRONCHIAL_ASTHMA);
            model.addAttribute(VEGETATIVE_VASCULAR_DYSTONIA, pupil.getCongenitalDiseases() == CongenitalDiseases.VEGETATIVE_VASCULAR_DYSTONIA);
            model.addAttribute(CEREBRAL_PALSY, pupil.getCongenitalDiseases() == CongenitalDiseases.CEREBRAL_PALSY);
            model.addAttribute(DEVELOPMENT_DELAY, pupil.getCongenitalDiseases() == CongenitalDiseases.DEVELOPMENT_DELAY);
            model.addAttribute("sessionId", sessionOptional.get().getUuid());
            return "edit";
        }
        model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
        model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("UNKNOWN_ERROR"));
        return ERROR_PAGE_NAME;
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public String save(EditForm editForm, Model model){
        Optional<Session> optionalSession = sessionService.findByUuid(editForm.sessionId());
        if (optionalSession.isPresent()){
            if (optionalSession.get().isExpire()){
                model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_EXPIRE"));
                model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("SESSION_EXPIRE_GIVE_NEW"));
                return ERROR_PAGE_NAME;
            }
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_NOT_FOUND"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("SESSION_NOT_FOUND_GIVE_NEW"));
            return ERROR_PAGE_NAME;
        }
        Pupil pupil = optionalSession.get().getPupil();
        if (!editForm.name().isEmpty()) {
            pupil.setName(editForm.name());
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("EMPTY_FIELD_NAME"));
            return ERROR_PAGE_NAME;
        }
        if (!editForm.secondName().isEmpty()) {
            pupil.setSecondName(editForm.secondName());
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("EMPTY_FIELD_SECOND_NAME"));
            return ERROR_PAGE_NAME;
        }
        if (!editForm.lastName().isEmpty()) {
            pupil.setLastName(editForm.lastName());
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("EMPTY_FIELD_LAST_NAME"));
            return ERROR_PAGE_NAME;
        }
        if (editForm.classNumber() < 12 && editForm.classNumber() > 0) {
            pupil.setClassNumber(editForm.classNumber());
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("WRONG_CLASS_NUMBER"));
            return ERROR_PAGE_NAME;
        }
        if (!editForm.address().isEmpty()) {
            pupil.setAddress(editForm.address());
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("EMPTY_FIELD_ADDRESS"));
            return ERROR_PAGE_NAME;
        }
        if (!editForm.date_of_record().isEmpty()) {
            try {
                Date dateOfRecord = DateUtils.dateFormat.parse(editForm.date_of_record());
                pupil.setDateOfRecord(dateOfRecord);
            } catch (ParseException e) {
                log.info("unable to parse date of record", e);
                model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
                model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("DATE_FOMRAT_DATE_OF_RECORD"));
                return ERROR_PAGE_NAME;
            }
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("EMPTY_FIELD_DATE_OF_RECORD"));
            return ERROR_PAGE_NAME;
        }
        if (!editForm.date_of_birth().isEmpty()) {
            try {
                Date dateOfBirth = DateUtils.dateFormat.parse(editForm.date_of_birth());
                pupil.setDateOfBirth(dateOfBirth);
            } catch (ParseException e) {
                log.info("unable to parse date of record", e);
                model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
                model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("DATE_FORMAT_DATE_OF_BIRTH"));
                return ERROR_PAGE_NAME;
            }
        } else {
            model.addAttribute(ERROR_TITLE, resourceBundle.getString("ERROR"));
            model.addAttribute(ERROR_MESSAGE, resourceBundle.getString("EMPTY_FIELD_DATE_OF_BIRTH"));
            return ERROR_PAGE_NAME;
        }
        if (TelephoneUtils.validate(editForm.telephone())) {
            pupil.setTelephone(editForm.telephone());
        }
        if (TelephoneUtils.validate(editForm.telephone_mother())) {
            pupil.setTelephoneMother(editForm.telephone_mother());
        }
        if (TelephoneUtils.validate(editForm.telephone_father())) {
            pupil.setTelephoneFather(editForm.telephone_father());
        }
        if (TelephoneUtils.validate(editForm.telephone_grandmother())) {
            pupil.setTelephoneGrandMother(editForm.telephone_grandmother());
        }
        if (EmailValidator.getInstance().isValid(editForm.email())) {
            pupil.setEmail(editForm.email());
        }
        if (!editForm.place_of_work_mother().isEmpty()) {
            pupil.setPlaceOfWorkMother(editForm.place_of_work_mother());
        }
        if (!editForm.place_of_work_father().isEmpty()) {
            pupil.setPlaceOfWorkFather(editForm.place_of_work_father());
        }
        if (!editForm.mother_name().isEmpty()) {
            pupil.setMotherName(editForm.mother_name());
        }
        if (!editForm.father_name().isEmpty()) {
            pupil.setFatherName(editForm.father_name());
        }
        if (!editForm.grandmother_name().isEmpty()) {
            pupil.setGrandMotherName(editForm.grandmother_name());
        }
        switch (editForm.diseases()) {
            case "none" -> pupil.setCongenitalDiseases(CongenitalDiseases.NONE);
            case BRONCHIAL_ASTHMA -> pupil.setCongenitalDiseases(CongenitalDiseases.BRONCHIAL_ASTHMA);
            case VEGETATIVE_VASCULAR_DYSTONIA -> pupil.setCongenitalDiseases(CongenitalDiseases.VEGETATIVE_VASCULAR_DYSTONIA);
            case CEREBRAL_PALSY -> pupil.setCongenitalDiseases(CongenitalDiseases.CEREBRAL_PALSY);
            case DEVELOPMENT_DELAY -> pupil.setCongenitalDiseases(CongenitalDiseases.DEVELOPMENT_DELAY);
        }
        if (editForm.chemistry().isPresent() && editForm.chemistry().get().equals("on")) {
            pupil.getSubjects().add(Subject.CHEMISTRY);
        }
        if (editForm.biology().isPresent() && editForm.biology().get().equals("on")) {
            pupil.getSubjects().add(Subject.BIOLOGY);
        }
        if (editForm.german().isPresent() && editForm.german().get().equals("on")) {
            pupil.getSubjects().add(Subject.GERMAN);
        }
        if (editForm.english().isPresent() && editForm.english().get().equals("on")) {
            pupil.getSubjects().add(Subject.ENGLISH);
        }
        if (editForm.primary_classes().isPresent() && editForm.primary_classes().get().equals("on")) {
            pupil.getSubjects().add(Subject.PRIMARY_CLASSES);
        }
        if (editForm.russian().isPresent() && editForm.russian().get().equals("on")) {
            pupil.getSubjects().add(Subject.RUSSIAN);
        }
        if (editForm.mathematics().isPresent() && editForm.mathematics().get().equals("on")) {
            pupil.getSubjects().add(Subject.MATHEMATICS);
        }
        if (editForm.social_studies().isPresent() && editForm.social_studies().get().equals("on")) {
            pupil.getSubjects().add(Subject.SOCIAL_STUDIES);
        }
        if (editForm.history().isPresent() && editForm.history().get().equals("on")) {
            pupil.getSubjects().add(Subject.HISTORY);
        }
        if (editForm.geography().isPresent() && editForm.geography().get().equals("on")) {
            pupil.getSubjects().add(Subject.GEOGRAPHY);
        }
        if (editForm.speech_therapy().isPresent() && editForm.speech_therapy().get().equals("on")) {
            pupil.getSubjects().add(Subject.SPEECH_THERAPIST);
        }
        if (editForm.psychology().isPresent() && editForm.psychology().get().equals("on")) {
            pupil.getSubjects().add(Subject.PSYCHOLOGY);
        }
        pupil.setUpdateBy(optionalSession.get().getUpdateBy());
        pupilService.save(pupil);
        model.addAttribute("successMessage", resourceBundle.getString("PUPIL_UPDATED"));
        return "success";
    }

}