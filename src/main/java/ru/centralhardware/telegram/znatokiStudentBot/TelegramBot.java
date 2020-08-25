package ru.centralhardware.telegram.znatokiStudentBot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.starter.AfterBotRegistration;
import ru.centralhardware.telegram.znatokiStudentBot.Builder.InlineKeyboardBuilder;
import ru.centralhardware.telegram.znatokiStudentBot.Builder.ReplyKeyboardBuilder;
import ru.centralhardware.telegram.znatokiStudentBot.Command.*;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.*;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Lesson;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Payment;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;
import ru.centralhardware.telegram.znatokiStudentBot.Service.*;
import ru.centralhardware.telegram.znatokiStudentBot.Steps.AddLessonsSteps;
import ru.centralhardware.telegram.znatokiStudentBot.Steps.AddPaymentSteps;
import ru.centralhardware.telegram.znatokiStudentBot.Steps.AddPupilSteps;
import ru.centralhardware.telegram.znatokiStudentBot.Util.DateUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.StringUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelephoneUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("deprecation")
@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {

    private static final SimpleDateFormat dateFormat    = new SimpleDateFormat("dd MM yyyy");
    public static final String REGISTER_COMMAND         = "register command - %s ";
    public static final String CALLBACK_EMAIL_PREFIX    = "email_";
    private static final String ADD_COMMAND             = "/add";
    public static final String USER_INFO_COMMAND        = "/user_info";
    public static final String EDIT_USER_COMMAND        = "/edit_user";
    public static final String DELETE_USER_COMMAND      = "/delete_user";
    private static final String CANCEL_COMMAND          = "/cancel";
    private static final String SKIP_COMMAND            = "/skip";

    @Autowired
    private StartCommand        startCommand;
    @Autowired
    private GetEmailList        getEmailList;
    @Autowired
    private SearchCommand       searchCommand;
    @Autowired
    private UserInfoCommand     userInfoCommand;
    @Autowired
    private GrandAccessCommand  grandAccessCommand;
    @Autowired
    private GetIdCommand        getIdCommand;
    @Autowired
    private ShowPaymentCommand  showPaymentCommand;
    @Autowired
    private ShowTimetable       showTimetable;
    @Autowired
    private SendEmailAll        sendEmailAll;
    @Autowired
    private GetTelephoneList    getTelephoneList;
    @Autowired
    private Ping                ping;
    @Autowired
    private PupilService        pupilService;
    @Autowired
    private TelegramService     telegramService;
    @Autowired
    private SessionService      sessionService;
    @Autowired
    private EmailService        emailService;
    @Autowired
    private MailSender          mailSender;
    @Autowired
    private PaymentService      paymentService;
    @Autowired
    private LessonService       lessonService;
    @Autowired
    private StatisticService    statisticService;
    @Autowired
    private TelegramUtils       telegramUtils;
    @Autowired
    private ResourceBundle      resourceBundle;
    private final Map<Long, AddDetail<AddPupilSteps, Pupil>> addDetailMap       = new HashMap<>(3);
    private final Map<Long, AddDetail<AddPaymentSteps, Payment>> addPaymentMap  = new HashMap<>(3);
    private final Map<Long, AddDetail<AddLessonsSteps, Lesson>> addLessonMap    = new HashMap<>(3);


    private final static DefaultBotOptions botOptions = new DefaultBotOptions();

    static {
        botOptions.setBaseUrl(Config.TELEGRAM_API_BOT_URL);
    }

    public TelegramBot() {
        super(botOptions);
    }

    @AfterBotRegistration
    public void after_register(){
        log.info(String.format(REGISTER_COMMAND, StartCommand.class));
        register(startCommand);

        log.info(String.format(REGISTER_COMMAND, GetEmailList.class));
        register(getEmailList);

        log.info(String.format(REGISTER_COMMAND, GetTelephoneList.class));
        register(getTelephoneList);

        log.info(String.format(REGISTER_COMMAND, SearchCommand.class));
        register(searchCommand);

        log.info(String.format(REGISTER_COMMAND, UserInfoCommand.class));
        register(userInfoCommand);

        log.info(String.format(REGISTER_COMMAND, GrandAccessCommand.class));
        register(grandAccessCommand);

        log.info(String.format(REGISTER_COMMAND, GetIdCommand.class));
        register(getIdCommand);

        log.info(String.format(REGISTER_COMMAND, ShowPaymentCommand.class));
        register(showPaymentCommand);

        log.info(String.format(REGISTER_COMMAND, ShowTimetable.class));
        register(showTimetable);

        log.info(String.format(REGISTER_COMMAND, Ping.class));
        register(ping);

        log.info(String.format(REGISTER_COMMAND, HelpCommand.class));
        register(new HelpCommand());

        if (Config.getEmailEnabled()){
            log.info(String.format(REGISTER_COMMAND, SendEmailAll.class));
            register(sendEmailAll);
        }

        log.info("register telegram bot");
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            processTextMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            processCallback(update.getCallbackQuery());
        }
    }

    private boolean checkCancel(Long chatId, String message) {
        if (message.equals(CANCEL_COMMAND)){
            addDetailMap.remove(chatId);
            telegramUtils.sendMessage(resourceBundle.getString("CANCEL_ADD_PUPIL"), chatId);
            return true;
        } else {
            return false;
        }
    }

    private void processTextMessage(Message message){
        Long chatId = message.getChatId();
        String text = message.getText();
        User user   = message.getFrom();

        log.info(String.format("receive message %s from user %s %s %s",
                text,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        telegramService.update(chatId, message.getFrom());
        if (telegramService.isUnauthorized(chatId) || !telegramService.hasWriteRight(chatId)){
            telegramUtils.sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
            return;
        }
        if (text.equals(ADD_COMMAND)){
            statisticService.save(Action.EXECUTE_COMMAND_ADD_PUPIL, chatId);
            addDetailMap.put(chatId, new AddDetail(AddPupilSteps.INPUT_FIO, new Pupil()));
            telegramUtils.sendMessageAndRemoveKeyboard(resourceBundle.getString("INPUT_FIO_IN_FORMAT"), chatId);
        } else if (addDetailMap.get(chatId) != null && addDetailMap.get(chatId).getSteps() != null){
            switch (addDetailMap.get(chatId).getSteps()){
                case INPUT_FIO -> {
                    if (checkCancel(chatId, text)) return;

                    String[] words = text.split(" ");
                    if (!(words.length >= 2 && words.length <= 3)) {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_FIO_REQUIRED_FORMAT"), chatId);
                        return;
                    }
                    Pupil pupil = addDetailMap.get(chatId).getDetails();
                    if (words.length == 3){
                        pupil.setSecondName(words[0]);
                        pupil.setName(words[1]);
                        pupil.setLastName(words[2]);
                    } else {
                        pupil.setSecondName(words[0]);
                        pupil.setName(words[1]);
                        pupil.setLastName("");
                    }
                    if (pupilService.checkExistenceByFio(pupil.getName(), pupil.getSecondName(), pupil.getLastName())){
                        telegramUtils.sendMessage(resourceBundle.getString("FIO_ALREADY_IN_DATABASE"), chatId);
                        return;
                    }
                    addDetailMap.get(chatId).nextStep();
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_CLASS"), chatId);
                }
                case INPUT_CLASS_NUMBER -> {
                    if (checkCancel(chatId, text)) return;

                    if (StringUtils.isNumeric(text) || text.equals("-1")){
                        int classNumber = Integer.parseInt(text);
                        if (classNumber > 11 || classNumber < 1 && classNumber != -1){
                            telegramUtils.sendMessage(resourceBundle.getString("CLASS_MUST_BE_IN_RANGE"), chatId);
                            return;
                        }
                        addDetailMap.get(chatId).getDetails().setClassNumber(classNumber);
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_ADDRESS"), chatId);
                        addDetailMap.get(chatId).nextStep();
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("NECESSERY_TO_INPUT_NUMBER"), chatId);
                    }
                }
                case INPUT_ADDRESS -> {
                    if (checkCancel(chatId, text)) return;

                    addDetailMap.get(chatId).getDetails().setAddress(text);
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_DATE_IN_FORMAT"), chatId);
                    addDetailMap.get(chatId).nextStep();
                }
                case INPUT_DATE_OF_RECORD -> {
                    if (checkCancel(chatId, text)) return;

                    try {
                        Date date = dateFormat.parse(text);
                        if (date.getYear() < 119){
                            telegramUtils.sendMessage(resourceBundle.getString("YEAR_OF_RECORD_LOW_THEN_2019"), chatId);
                            return;
                        }
                        addDetailMap.get(chatId).getDetails().setDateOfRecord(date);
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_DATE_OF_BIRTH_IN_FORMAT"), chatId);
                    } catch (ParseException e) {
                        log.info("", e);
                        telegramUtils.sendMessage(resourceBundle.getString("DATE_FORMAT_ERROR"), chatId);
                    }
                }
                case INPUT_DATE_OF_BIRTH -> {
                    if (checkCancel(chatId, text)) return;

                    try {
                        Date date = dateFormat.parse(text);
                        if (date.getYear() < 90){
                            telegramUtils.sendMessage(resourceBundle.getString("YEAR_OF_BIRTH_LOW_THEN_1990"), chatId);
                            return;
                        }
                        addDetailMap.get(chatId).getDetails().setDateOfBirth(date);
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_PUPIL_TEL"), chatId);
                    } catch (ParseException e) {
                        log.info("", e);
                        telegramUtils.sendMessage(resourceBundle.getString("DATE_FORMAT_ERROR"), chatId);
                    }
                }
                case INPUT_TELEPHONE -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        addDetailMap.get(chatId).getDetails().setTelephone("");
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_FATHER_TEL"), chatId);
                        return;
                    }
                    if (TelephoneUtils.validate(text)){
                        if (pupilService.existByTelephone(text)){
                            telegramUtils.sendMessage(resourceBundle.getString("TEL_ALREADY_EXIST"), chatId);
                            return;
                        }
                        addDetailMap.get(chatId).getDetails().setTelephone(text);
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_FATHER_TEL"), chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_RIGHT_TEL_NUMBER"), chatId);
                    }
                }
                case INPUT_TELEPHONE_FATHER -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_MOTHER_TEL"), chatId);
                        return;
                    }
                    if (!pupilService.canInsertTelephoneFather(text) ||
                            addDetailMap.get(chatId).getDetails().checkTelephoneUnique(text)){
                        telegramUtils.sendMessage(resourceBundle.getString("TEL_ALREADY_EXIST"), chatId);
                        return;
                    }
                    if (TelephoneUtils.validate(text)){
                        addDetailMap.get(chatId).getDetails().setTelephoneFather(text);
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_MOTHER_TEL"), chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_RIGHT_TEL_NUMBER"), chatId);
                    }
                }
                case INPUT_TELEPHONE_MOTHER -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_GRANDMOTHER_TEL"), chatId);
                        return;
                    }
                    if (!pupilService.canInsertTelephoneMother(text) || addDetailMap.get(chatId).getDetails().checkTelephoneUnique(text)){
                        telegramUtils.sendMessage(resourceBundle.getString("TEL_ALREADY_EXIST"), chatId);
                        return;
                    }
                    if (TelephoneUtils.validate(text)){
                        addDetailMap.get(chatId).getDetails().setTelephoneMother(text);
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_GRANDMOTHER_TEL"), chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_RIGHT_TEL_NUMBER"), chatId);
                    }
                }
                case INPUT_TELEPHONE_GRAND_MOTHER -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_EMAIL"), chatId);
                        return;
                    }
                    if (!pupilService.canInsertTelephoneGrandMother(text) || addDetailMap.get(chatId).getDetails().checkTelephoneUnique(text)){
                        telegramUtils.sendMessage(resourceBundle.getString("TEL_ALREADY_EXIST"), chatId);
                        return;
                    }
                    if (TelephoneUtils.validate(text)){
                        addDetailMap.get(chatId).getDetails().setTelephoneGrandMother(text);
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_EMAIL"), chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_RIGHT_TEL_NUMBER"), chatId);
                    }
                }
                case INPUT_EMAIL -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                                create().
                                setChatId(chatId).
                                setText(resourceBundle.getString("INPUT_SUBJECTS"));
                        for (Subject subject : Subject.values()){
                            replyKeyboardBuilder.
                                    row().button(subject.getRusName()).endRow();
                        }
                        telegramUtils.sendMessage(replyKeyboardBuilder.build());
                        return;
                    }
                    if (EmailValidator.getInstance().isValid(text)){
                        if (pupilService.existByEmail(text)){
                            telegramUtils.sendMessage(resourceBundle.getString("EMAIL_ALREADY_IN_DATABASE"), chatId);
                            return;
                        }
                        addDetailMap.get(chatId).getDetails().setEmail(text);
                        addDetailMap.get(chatId).nextStep();
                        ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                                create().
                                setChatId(chatId).
                                setText(resourceBundle.getString("INPUT_SUBJECTS"));
                        for (Subject subject : Subject.values()){
                            replyKeyboardBuilder.
                                    row().button(subject.getRusName()).endRow();
                        }
                        telegramUtils.sendMessage(replyKeyboardBuilder.build());
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_VALID_EMAIL"), chatId);
                    }
                }
                case INPUT_SUBJECT -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals("/complete")){
                        addDetailMap.get(chatId).nextStep();
                        ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                                create().
                                setChatId(chatId).
                                setText(resourceBundle.getString("INPUT_HOW_TO_KNOW"));
                        for (HowToKnow howToKnow : HowToKnow.values()){
                            replyKeyboardBuilder.
                                    row().button(howToKnow.getRusName()).endRow();
                        }
                        telegramUtils.sendMessage(replyKeyboardBuilder.build());
                        return;
                    }
                    if (Subject.validate(text)){
                        addDetailMap.get(chatId).getDetails().getSubjects().add(Subject.getConstant(text));
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("SUBJECT_NOT_FOUND"), chatId);
                    }
                }
                case INPUT_HOW_TO_KNOW -> {
                    if (checkCancel(chatId, text)) return;

                    if (HowToKnow.validate(text)){
                        addDetailMap.get(chatId).getDetails().setHowToKnow(HowToKnow.getConstant(text));
                        addDetailMap.get(chatId).nextStep();
                        ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                                create().
                                setChatId(chatId).
                                setText(resourceBundle.getString("INPUT_DISEASES"));
                        for (CongenitalDiseases congenitalDiseases : CongenitalDiseases.values()){
                            replyKeyboardBuilder.
                                    row().button(congenitalDiseases.getRusName()).endRow();
                        }
                        telegramUtils.sendMessage(replyKeyboardBuilder.build());
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("NOT_FOUND"), chatId);
                    }
                }
                case INPUT_CONGENITAL_DISEASES -> {
                    if (checkCancel(chatId, text)) return;

                    if (CongenitalDiseases.validate(text)){
                        addDetailMap.get(chatId).getDetails().setCongenitalDiseases(CongenitalDiseases.getConstant(text));
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessageAndRemoveKeyboard(resourceBundle.getString("INPUT_PLACE_OF_WORK_MOTHER"), chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("DISEASES_NOT_FOUND"), chatId);
                    }
                }
                case INPUT_PLACE_OF_WORK_MOTHER -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_PLACE_OF_WORK_FATHER"), chatId);
                        return;
                    }
                    addDetailMap.get(chatId).getDetails().setPlaceOfWorkMother(text);
                    addDetailMap.get(chatId).nextStep();
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_PLACE_OF_WORK_FATHER"), chatId);
                }
                case INPUT_PLACE_OF_WORK_FATHER -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_MOTHER_NAME"), chatId);
                        return;
                    }
                    addDetailMap.get(chatId).getDetails().setPlaceOfWorkFather(text);
                    addDetailMap.get(chatId).nextStep();
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_MOTHER_NAME"), chatId);
                }
                case INPUT_MOTHER_NAME -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_FATHER_NAME"), chatId);
                        return;
                    }
                    addDetailMap.get(chatId).getDetails().setMotherName(text);
                    addDetailMap.get(chatId).nextStep();
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_FATHER_NAME"), chatId);
                }
                case INPUT_FATHER_NAME -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_HOBBIES"), chatId);
                        return;
                    }
                    addDetailMap.get(chatId).getDetails().setFatherName(text);
                    addDetailMap.get(chatId).nextStep();
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_HOBBIES"), chatId);
                }
                case INPUT_HOBBIES -> {
                    if (checkCancel(chatId, text)) return;

                    if (text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_GRANDMOTHER_NAME"), chatId);
                        return;
                    }
                    addDetailMap.get(chatId).getDetails().setHobbies(text);
                    addDetailMap.get(chatId).nextStep();
                    telegramUtils.sendMessage(resourceBundle.getString("INPUT_GRANDMOTHER_NAME"), chatId);
                }
                case INPUT_GRAND_MOTHER_NAME -> {
                    if (checkCancel(chatId, text)) return;

                    if (!text.equals(SKIP_COMMAND)){
                        addDetailMap.get(chatId).getDetails().setGrandMotherName(text);
                    }
                    addDetailMap.get(chatId).getDetails().setCreatedBy(telegramService.findById(chatId).get());
                    telegramUtils.sendMessageWithMarkdown(pupilService.save(addDetailMap.get(chatId).getDetails()).toString(), chatId);
                    addDetailMap.remove(chatId);
                    telegramUtils.sendMessage(resourceBundle.getString("CREATE_PUPIL_FINISHED"), chatId);
                }
            }
        } else if (addPaymentMap.get(chatId) != null && addPaymentMap.get(chatId).getSteps() != null){
            switch (addPaymentMap.get(chatId).getSteps()){
                case INPUT_SUBJECT -> {
                    if (Subject.validate(text)){
                        addPaymentMap.get(chatId).getDetails().setSubject(Subject.getConstant(text));
                        addPaymentMap.get(chatId).nextStep();
                        ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                                create().
                                setText(resourceBundle.getString("INPUT_HOW_TO_PAY")).
                                setChatId(chatId);
                        for (HowToPay howToPay : HowToPay.values()){
                            replyKeyboardBuilder.row().button(howToPay.getRusName()).endRow();
                        }
                        telegramUtils.sendMessage(replyKeyboardBuilder.build());
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("NOT_FOUND"), chatId);
                    }
                }
                case INPUT_HOW_TO_PAY -> {
                    if (HowToPay.validate(text)){
                        addPaymentMap.get(chatId).getDetails().setHowToPay(HowToPay.getConstant(text));
                        addPaymentMap.get(chatId).nextStep();
                        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                        telegramUtils.sendMessageAndRemoveKeyboard(resourceBundle.getString("INPUT_DATE_IN_FORMAt"), chatId);
                    } else {
                        telegramUtils.sendMessageAndRemoveKeyboard(resourceBundle.getString("NOT_FOUND"), chatId);
                    }
                }
                case INPUT_DATE_OF_PAY -> {
                    try{
                        Date date = dateFormat.parse(text);
                        addPaymentMap.get(chatId).getDetails().setDateOfPayment(date);
                        addPaymentMap.get(chatId).nextStep();
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_SUMM"), chatId);
                    } catch (ParseException e) {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_DATE_IN_FORMAt"), chatId);
                    }
                }
                case INPUT_AMOUNT -> {
                    if (StringUtils.isNumeric(text)){
                        addPaymentMap.get(chatId).getDetails().setAmount(Integer.parseInt(text));
                        Payment payment = paymentService.save(addPaymentMap.get(chatId).getDetails());
                        addPaymentMap.remove(chatId);
                        telegramUtils.sendMessageWithMarkdown(payment.toString(), chatId);
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_PAYMENT_COMPLETE"), chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("INPUT_SUMM_IN_RUBLE"), chatId);
                    }
                }
            }
        } else if (addLessonMap.get(chatId) != null && addLessonMap.get(chatId).getSteps() != null){
            switch (addLessonMap.get(chatId).getSteps()){
                case INPUT_SUBJECT -> {
                    if (Subject.validate(text)){
                        addLessonMap.get(chatId).getDetails().setSubject(Subject.getConstant(text));
                        addLessonMap.get(chatId).nextStep();
                        telegramUtils.sendMessageAndRemoveKeyboard("Введите дату урока в формате: dd MM yyyy /today /tomorrow", chatId);
                    } else {
                        telegramUtils.sendMessage(resourceBundle.getString("NOT_FOUND"), chatId);
                    }
                }
                case INPUT_DATE -> {
                    try{
                        if (text.equals("/today")){
                            addLessonMap.get(chatId).getDetails().setDate(dateFormat.parse(dateFormat.format(new Date())));
                        } else if (text.equals("/tomorrow")){
                            addLessonMap.get(chatId).getDetails().setDate(dateFormat.parse(dateFormat.format(DateUtils.getNextDay(new Date()))));
                        } else {
                            Date date = dateFormat.parse(text);
                            if (!date.after(new Date(System.currentTimeMillis()-24*60*60*1000))){
                                telegramUtils.sendMessage("дата не должна быть в прошлом", chatId);
                                return;
                            }
                            addLessonMap.get(chatId).getDetails().setDate(date);
                        }
                        addLessonMap.get(chatId).nextStep();
                        ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                                create().
                                setChatId(chatId).setText("Выберите время");
                        for (LessonTime lessonTime : LessonTime.values()){
                            replyKeyboardBuilder.row().button(lessonTime.getRusName()).endRow();
                        }
                        telegramUtils.sendMessage(replyKeyboardBuilder.build());
                    } catch (ParseException e) {
                        telegramUtils.sendMessage("Введите дату урока в формате: dd MM yyyy", chatId);
                    }
                }
                case INPUT_TIME -> {
                    if (LessonTime.validate(text)){
                        addLessonMap.get(chatId).getDetails().setLessonTime(LessonTime.getConstant(text));
                        Lesson lesson = lessonService.save(addLessonMap.get(chatId).getDetails());
                        telegramUtils.sendMessageWithMarkdownAndRemoveKeyboard(lesson.toString(), chatId);
                        telegramUtils.sendMessageAndRemoveKeyboard("Добавление урока закончено", chatId);
                        addLessonMap.remove(chatId);
                    }
                }
            }
        }
    }

    private void processCallback(CallbackQuery callbackQuery){
        Long chatId = callbackQuery.getMessage().getChatId();
        if (callbackQuery.getData().startsWith(CALLBACK_EMAIL_PREFIX)){
            statisticService.save(Action.PROCESS_CALLBACK_EMAIL, chatId);
            if (emailService.checkByUuid(callbackQuery.getData().replace(CALLBACK_EMAIL_PREFIX, ""))){
                emailService.findByUuid(callbackQuery.getData().replace(CALLBACK_EMAIL_PREFIX, "")).ifPresent(email -> {
                    mailSender.sendAll(email.getText(), email.getSubject());
                    telegramUtils.sendMessage(resourceBundle.getString("MAILING_STARTED"), chatId);
                });
            }
        } else if (callbackQuery.getData().startsWith(USER_INFO_COMMAND)){
            statisticService.save(Action.PROCESS_CALLBACK_USER_INFO, chatId);
            if (!telegramService.hasReadRight(chatId)){
                telegramUtils.sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
                return;
            }
            Optional<Pupil> pupilOptional = pupilService.findById(Integer.parseInt(callbackQuery.getData().replace(USER_INFO_COMMAND,"")));
            if (pupilOptional.isPresent()){
                telegramUtils.sendMessageWithMarkdown(pupilOptional.get().toString(), chatId);
            } else {
                telegramUtils.sendMessage(resourceBundle.getString("USER_NOT_FOUND"), chatId);
            }
        } else if (callbackQuery.getData().startsWith(EDIT_USER_COMMAND)){
            statisticService.save(Action.PROCESS_CALLBACK_EDIT_USER, chatId);
            if (!telegramService.hasWriteRight(chatId)){
                telegramUtils.sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
                return;
            }
            pupilService.findById(Integer.parseInt(callbackQuery.getData().replace(EDIT_USER_COMMAND,""))).ifPresent(pupil -> {
                String uuid = sessionService.create(pupil, telegramService.findById(chatId).get());
                InlineKeyboardBuilder inlineKeyboardBuilder = InlineKeyboardBuilder.
                        create().
                        setChatId(chatId).
                        setText(resourceBundle.getString("EDIT")).
                        row().button(resourceBundle.getString("OPEN"), "sdf", String.format("%s/edit?sessionId=%s", Config.getBaseUrl(),uuid )).endRow();
                log.info("generate edit link {}", String.format("%s/edit?sessionId=%s", Config.getBaseUrl(), uuid));
                telegramUtils.sendMessage(inlineKeyboardBuilder.build());
            });
        } else if (callbackQuery.getData().startsWith(DELETE_USER_COMMAND)){
            statisticService.save(Action.PROCESS_CALLBACK_DELETE_USER, chatId);
            if (!telegramService.isAdmin(chatId)) {
                telegramUtils.sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
                return;
            }
            pupilService.findById(Integer.parseInt(callbackQuery.getData().replace(DELETE_USER_COMMAND,""))).ifPresent(pupil -> {
                pupil.setDeleted(true);
                pupilService.save(pupil);
                telegramUtils.sendMessage(resourceBundle.getString("PUPIL_DELETED"), chatId);
            });
        } else if (callbackQuery.getData().startsWith(SearchCommand.ADD_PAYMENT_CALLBACK_PREFIX)){
            statisticService.save(Action.PROCESS_CALLBACK_ADD_PAYMENT, chatId);
            if (!telegramService.hasWriteRight(chatId)){
                telegramUtils.sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
                return;
            }
            pupilService.findById(Integer.parseInt(callbackQuery.getData().replace(SearchCommand.ADD_PAYMENT_CALLBACK_PREFIX, ""))).ifPresent(pupil ->{
                addPaymentMap.put(chatId, new AddDetail<>(AddPaymentSteps.INPUT_SUBJECT, new Payment(telegramService.findById(chatId).get(), pupil)));
                ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                        create().
                        setText(resourceBundle.getString("CHOOSE_SUBJECT")).
                        setChatId(chatId);
                for (Subject subject : Subject.values()){
                    replyKeyboardBuilder.row().button(subject.getRusName()).endRow();
                }
                telegramUtils.sendMessage(replyKeyboardBuilder.build());
            });
        } else if (callbackQuery.getData().startsWith("/add_lesson")){
            statisticService.save(Action.PROCESS_CALLBACK_ADD_LESSON, chatId);
            if (!telegramService.hasWriteRight(chatId)){
                telegramUtils.sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
                return;
            }
            pupilService.findById(Integer.parseInt(callbackQuery.getData().replace("/add_lesson", ""))).ifPresent(pupil -> {
                addLessonMap.put(chatId, new AddDetail<>(AddLessonsSteps.INPUT_SUBJECT, new Lesson(telegramService.findById(chatId).get(), pupil)));
                ReplyKeyboardBuilder replyKeyboardBuilder = ReplyKeyboardBuilder.
                        create().
                        setText(resourceBundle.getString("CHOOSE_SUBJECT")).
                        setChatId(chatId);
                for (Subject subject : Subject.values()){
                    replyKeyboardBuilder.row().button(subject.getRusName()).endRow();
                }
                telegramUtils.sendMessage(replyKeyboardBuilder.build());
            });
        } else if (callbackQuery.getData().startsWith("/lesson_processed")){
            statisticService.save(Action.PROCESS_CALLBACK_LESSON_PROCESS, chatId);
            lessonService.findById(Integer.parseInt(callbackQuery.getData().replace("/lesson_processed", ""))).ifPresent(it -> {
                it.setProcessed(true);
                lessonService.save(it);
                telegramUtils.sendMessage(resourceBundle.getString("MARK_AS_EXECUTED"), chatId);
            });
        }
    }

    @Override
    public String getBotUsername() {
        return Config.getTelegramUsername();
    }

    @Override
    public String getBotToken() {
        return Config.getTelegramToken();
    }
}
