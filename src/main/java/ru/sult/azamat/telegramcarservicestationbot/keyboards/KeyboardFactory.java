package ru.sult.azamat.telegramcarservicestationbot.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static ru.sult.azamat.telegramcarservicestationbot.enums.TypeOfWorks.BODY;

public class KeyboardFactory {

    private KeyboardFactory() {
    }

    public static ReplyKeyboard getTypeOfWorks() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Двигатель");
        row1.add(BODY.getDesc());
        row1.add("Ходовая");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Автомойка");
        row2.add("Авто-электрик");
        row2.add("Шиномонтаж");
        return new ReplyKeyboardMarkup(List.of(row1, row2));
    }

}