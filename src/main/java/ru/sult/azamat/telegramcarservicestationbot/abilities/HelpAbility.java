package ru.sult.azamat.telegramcarservicestationbot.abilities;

import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.util.AbilityExtension;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@RequiredArgsConstructor
public class HelpAbility implements AbilityExtension {

    private final AbilityBot extensionUser;

    public Ability getHelp() {
        return Ability.builder()
                .name("help")
                .info("List of CarServiceStationBot commands")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> extensionUser.silent().send("/start - Запускает бота\n/stop - Останавливает бота", ctx.chatId()))
                .build();
    }
}