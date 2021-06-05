package ru.centralhardware.telegram.znatokiStudentBot.Builder;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboardBuilder {

    private Long chatId;
    private String text;

    private final List<KeyboardRow>  keyboard = new ArrayList<>();
    private KeyboardRow row  = null;

    private ReplyKeyboardBuilder() { }

    public static ReplyKeyboardBuilder create() {
        return new ReplyKeyboardBuilder();
    }

    public ReplyKeyboardBuilder setChatId(@NonNull Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public ReplyKeyboardBuilder setText(@NonNull String text){
        this.text = text;
        return this;
    }

    public ReplyKeyboardBuilder row() {
        row = new KeyboardRow();
        return this;
    }

    public ReplyKeyboardBuilder button(@NonNull String text) {
        row.add(text);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ReplyKeyboardBuilder endRow() {
        keyboard.add(row);
        row = null;
        return this;
    }

    public SendMessage build() {
        return SendMessage.
                builder().
                chatId(chatId.toString()).
                text(text).
                replyMarkup(ReplyKeyboardMarkup.
                        builder().
                        keyboard(keyboard).
                        build()).
                build();
    }

}
