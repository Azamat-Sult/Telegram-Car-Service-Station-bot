package ru.sult.azamat.telegramcarservicestationbot.responsehandlers;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.sult.azamat.telegramcarservicestationbot.enums.UserState;

import java.util.Map;

import static ru.sult.azamat.telegramcarservicestationbot.enums.UserState.AWAITING_NAME;
import static ru.sult.azamat.telegramcarservicestationbot.enums.UserState.FINISH;

public class ResponseHandler {

    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap("chatStates");
    }

    public void replyToStart(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Добро пожаловать в автосервис \"Гайки и болты\".\nКак к вам обращаться?");
        sender.execute(message);
        chatStates.put(chatId, AWAITING_NAME);
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }

        switch (chatStates.get(chatId)) {
            case AWAITING_NAME -> replyToName(chatId, message);
            default -> unexpectedMessage(chatId);
        }
    }

    private void replyToName(long chatId, Message message) {
        SendMessage reply = new SendMessage();
        reply.setChatId(chatId);
        reply.setText(message.getText() + ", что будем ремонтировать?");
        sender.execute(reply);
        chatStates.put(chatId, FINISH);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Спасибо что выбрали нас!. Будем рады видеть вас вновь!\nНаберите /start для новой записи на обслуживание вашего авто");
        chatStates.remove(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Сорян, я не понимаю...");
        sender.execute(sendMessage);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }
}