package ru.sult.azamat.telegramcarservicestationbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TypeOfWorks {

    BODY("Кузов");

    private final String desc;

    public TypeOfWorks getTypeOfWorkByDesc(String desc) {
        return Arrays.stream(values())
                .filter(typeOfWork -> typeOfWork.getDesc().equalsIgnoreCase(desc))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Не найден вид работ: \"%s\"", desc)));
    }
}