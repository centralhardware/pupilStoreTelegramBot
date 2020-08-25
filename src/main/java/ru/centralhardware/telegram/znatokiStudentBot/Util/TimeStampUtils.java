package ru.centralhardware.telegram.znatokiStudentBot.Util;

import java.sql.Timestamp;

public class TimeStampUtils {

    public static long getTimestamp() {
        return new Timestamp(System.currentTimeMillis()).getTime();
    }

}
