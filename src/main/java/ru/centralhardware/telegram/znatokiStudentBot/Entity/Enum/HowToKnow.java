package ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum;

import lombok.Getter;

public enum HowToKnow {

    SIGNBOARD("Вывеска"),
    FROM_PASTE_YEAR("С прошлых лет"),
    FROM_FIENDS("От знакомых"),
    FROM_2GIS("2gis"),
    ENTRANCE_ADVERTISE("Реклама на подъезде"),
    THE_ELDERS_WENT("Ходили старшие"),
    INTERNET("Интернет "),
    LEAFLET("Листовка"),
    AUDIO_ADVERTISE_IN_STORE("Аудио Реклама в магазине"),
    ADVERTISING_ON_TV("Реклама на ТВ");

    @Getter
    final String rusName;

    HowToKnow(String rusName) {
        this.rusName = rusName;
    }

    public static HowToKnow getConstant(String name) {
        for (HowToKnow howToKnow : values()) {
            if (howToKnow.rusName.equals(name)) return howToKnow;
        }
        return null;
    }

    public static boolean validate(String howToKnow) {
        for (HowToKnow how : values()) {
            if (how.rusName.equals(howToKnow)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return rusName;
    }
}
