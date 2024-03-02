package ru.sult.azamat.telegramcarservicestationbot.responsehandlers;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sult.azamat.telegramcarservicestationbot.entity.User;
import ru.sult.azamat.telegramcarservicestationbot.enums.TypeOfWorks;
import ru.sult.azamat.telegramcarservicestationbot.enums.UserState;
import ru.sult.azamat.telegramcarservicestationbot.keyboards.KeyboardFactory;
import ru.sult.azamat.telegramcarservicestationbot.service.UserService;

import java.util.Map;
import java.util.Optional;

import static ru.sult.azamat.telegramcarservicestationbot.enums.UserState.*;

public class ResponseHandler {

    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;
    private final UserService userService;

    private final AbilityBot bot;

    public ResponseHandler(SilentSender sender, DBContext db, UserService userService, AbilityBot bot) {
        this.sender = sender;
        chatStates = db.getMap("chatStates");
        this.userService = userService;
        this.bot = bot;
    }

    public void replyToStart(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        Optional<User> user = userService.getUserByChatId(chatId);
        if (user.isPresent()) {
            String username = user.get().getUsername();
            message.setText(username + ", рады видеть Вас вновь в автосервисе \"Гайки и болты\"");
            sender.execute(message);
            askTypeOfWork(chatId, username);
        } else {
            message.setText("Добро пожаловать в автосервис \"Гайки и болты\".\nКак к вам обращаться?");
            chatStates.put(chatId, AWAITING_NAME);
            sender.execute(message);
        }
    }

    public void replyToButtons(long chatId, Message message) {
        if ("/stop".equalsIgnoreCase(message.getText())) {
            stopChat(chatId);
        }

        switch (chatStates.get(chatId)) {
            case AWAITING_NAME -> replyToName(chatId, message);
            case AWAITING_TYPE_OF_WORK -> replyToTypeOfWork(chatId, message);
            case AWAITING_BODY_PHOTO -> replyToBodyPhoto(chatId, message);
            default -> unexpectedMessage(chatId);
        }
    }

    private void askTypeOfWork(long chatId, String userName) {
        promptWithKeyboardForState(chatId, userName + ", что будем ремонтировать?",
                KeyboardFactory.getTypeOfWorks(), AWAITING_TYPE_OF_WORK);
    }

    private void replyToName(long chatId, Message message) {
        promptWithKeyboardForState(chatId, message.getText() + ", что будем ремонтировать?",
                KeyboardFactory.getTypeOfWorks(), AWAITING_TYPE_OF_WORK);
        userService.create(message);
    }

    private void replyToTypeOfWork(long chatId, Message message) {
        String typeOfWorkDesc = message.getText();
        SendMessage reply = new SendMessage();
        reply.setChatId(chatId);
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        reply.setReplyMarkup(replyKeyboardRemove);
        if (TypeOfWorks.BODY.getDesc().equals(typeOfWorkDesc)) {
            reply.setText("Пожалуйста отправьте фото поврежденной детали");
            chatStates.put(chatId, AWAITING_BODY_PHOTO);
        } else {
            reply.setText(typeOfWorkDesc);
            chatStates.put(chatId, FINISH);
            stopChat(chatId);
        }
        sender.execute(reply);
    }

    private void replyToBodyPhoto(long chatId, Message message) {
        SendMessage reply = new SendMessage();
        reply.setChatId(chatId);
        if (message.hasPhoto()) {
            GetFile getFile = new GetFile(message.getPhoto().get(3).getFileId());
            try {
                File file = bot.execute(getFile);
                String userName = userService.getUserByChatId(chatId).get().getUsername();
                bot.downloadFile(file, new java.io.File("photos/" + chatId + "_" + userName + "_" + TypeOfWorks.BODY.getDesc() + ".jpg"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            reply.setText("Фото получили. Мастер по кузовному ремонту свяжется с вами");
        } else {
            reply.setText("Фото не получили");
        }
        sender.execute(reply);
        chatStates.put(chatId, FINISH);
        stopChat(chatId);
    }

    private void promptWithKeyboardForState(long chatId, String text, ReplyKeyboard keyboard, UserState userState) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        sender.execute(sendMessage);
        chatStates.put(chatId, userState);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Спасибо что выбрали нас! Будем рады видеть вас вновь!\nНаберите /start для новой записи на обслуживание вашего авто");
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