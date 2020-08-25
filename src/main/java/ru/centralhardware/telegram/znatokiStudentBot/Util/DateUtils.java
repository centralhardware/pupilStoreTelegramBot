package ru.centralhardware.telegram.znatokiStudentBot.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");

    public static Date getNextDay(Date date) {
        return new Date(date.getTime() + (1000 * 60 * 60 * 24));
    }

    public static Date getPreviousDay(Date date) {
        return new Date(date.getTime() - (1000 * 60 * 60 * 24));
    }

    public static boolean isBirthday(Date dateOfBirth) {
        return formatter.format(dateOfBirth).equals(formatter.format(new Date()));
    }

}
