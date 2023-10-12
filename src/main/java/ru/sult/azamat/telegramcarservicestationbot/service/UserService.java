package ru.sult.azamat.telegramcarservicestationbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sult.azamat.telegramcarservicestationbot.entity.User;
import ru.sult.azamat.telegramcarservicestationbot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User create(Message message) {
        User user = new User();
        user.setId(message.getChatId());
        user.setUsername(message.getText());
        repository.save(user);
        return user;
    }
}