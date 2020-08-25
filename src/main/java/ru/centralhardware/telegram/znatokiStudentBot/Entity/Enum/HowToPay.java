package ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum;

import lombok.Getter;

public enum HowToPay {

    CASH("наличными"),
    CARD("картой");

    @Getter
    final String rusName;

    HowToPay(String rusName) {
        this.rusName = rusName;
    }

    public static HowToPay getConstant(String name) {
        for (HowToPay howToPay : values()) {
            if (howToPay.rusName.equals(name)) return howToPay;
        }
        return null;
    }


    public static boolean validate(String subject) {
        for (HowToPay htp : values()) {
            if (htp.rusName.equals(subject)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return rusName;
    }

}
