package ru.sult.azamat.telegramcarservicestationbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sult.azamat.telegramcarservicestationbot.service.CarServiceStationBot;

@SpringBootApplication
public class TelegramCarServiceStationBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TelegramCarServiceStationBotApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(ctx.getBean("carServiceStationBot", CarServiceStationBot.class));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}