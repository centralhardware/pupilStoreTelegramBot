package ru.centralhardware.telegram.znatokiStudentBot.Builder;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardBuilder {

    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private Long chatId;
    private String text;
    private List<InlineKeyboardButton> row = null;

    private InlineKeyboardBuilder() { }

    public static InlineKeyboardBuilder create() {
        return new InlineKeyboardBuilder();
    }

    public InlineKeyboardBuilder setText(@NonNull String text) {
        this.text = text;
        return this;
    }

    public InlineKeyboardBuilder setChatId(@NonNull Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public InlineKeyboardBuilder row() {
        row = new ArrayList<>();
        return this;
    }

    public InlineKeyboardBuilder button(@NonNull String text, @NonNull String callbackData) {
        row.add(InlineKeyboardButton.
                builder().
                text(text).
                callbackData(callbackData).
                build());
        return this;
    }

    public InlineKeyboardBuilder button(@NonNull String text, @NonNull String callbackData, @NonNull String url) {
        row.add(InlineKeyboardButton.
                builder().
                text(text).
                callbackData(callbackData).
                url(url).
                build());
        return this;
    }

    public InlineKeyboardBuilder endRow() {
        keyboard.add(row);
        row = null;
        return this;
    }

    public SendMessage build() {
        return SendMessage.
                builder().
                chatId(String.valueOf(chatId)).
                text(text).
                replyMarkup(InlineKeyboardMarkup.
                        builder().
                        keyboard(keyboard).
                        build()).
                build();
    }

}
