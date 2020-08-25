package ru.centralhardware.telegram.znatokiStudentBot;

import lombok.Getter;

public class Config {
    /**
     * if you don't want to use local telegram api server, set to {@link org.telegram.telegrambots.meta.ApiConstants#BASE_URL}
     */
    public static final String TELEGRAM_API_BOT_URL = System.getenv("TELEGRAM_API_BOT_URL");
    @Getter
    private static final String telegramUsername    = System.getenv("TELEGRAM_USERNAME");
    @Getter
    private static final String telegramToken       = System.getenv("TELEGRAM_TOKEN");
    @Getter
    private static final Long adminId               = Long.valueOf(System.getenv("TELEGRAM_ADMIN"));
    @Getter
    private static final String baseUrl             = System.getenv("BASE_URL");
    @Getter
    private static final String baseHost            = System.getenv("BASE_HOST");
    @Getter
    private static final String smtpHost            = System.getenv("SMTP_HOST");
    @Getter
    private static final String smtpUser            = System.getenv("SMTP_USER");
    @Getter
    private static final String smtpPassword        = System.getenv("SMTP_PASSWORD");
    @Getter
    private static final int smtpPort               = Integer.parseInt(System.getenv("SMTP_PORT"));
    @Getter
    private static final String emailFrom           = System.getenv("EMAIL_FROM");
    @Getter
    private static final Boolean emailEnabled       = Boolean.valueOf(System.getenv("EMAIL_ENABLED"));
    @Getter
    private static final String birthText           = System.getenv("BIRTH_TEXT");
    @Getter
    private static final String tokenPath           = System.getenv("TOKEN_PATH");
    @Getter
    private static final boolean googleSheetEnable  = Boolean.parseBoolean(System.getenv("GOOGLE_SHEET_ENABLE"));
}
