package ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum;

import lombok.Getter;

public enum CongenitalDiseases {

    NONE("нету"),
    BRONCHIAL_ASTHMA("бронхиальная астма"),
    VEGETATIVE_VASCULAR_DYSTONIA("вегетососудистая дистония"),
    CEREBRAL_PALSY("дцп"),
    DEVELOPMENT_DELAY("задержка в развитие");

    @Getter
    final String rusName;

    CongenitalDiseases(String rusName) {
        this.rusName = rusName;
    }

    public static boolean validate(String congenial) {
        for (CongenitalDiseases congenitalDiseases : values()) {
            if (congenitalDiseases.rusName.equals(congenial)) return true;
        }
        return false;
    }

    public static CongenitalDiseases getConstant(String name) {
        for (CongenitalDiseases congenitalDiseases : values()) {
            if (congenitalDiseases.rusName.equals(name)) {
                return congenitalDiseases;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return rusName;
    }
}
