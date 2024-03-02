package ru.sult.azamat.telegramcarservicestationbot.bot;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sult.azamat.telegramcarservicestationbot.abilities.HelpAbility;
import ru.sult.azamat.telegramcarservicestationbot.responsehandlers.ResponseHandler;
import ru.sult.azamat.telegramcarservicestationbot.service.UserService;

import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Flag.PHOTO;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class Bot extends AbilityBot {

    public Bot(Environment environment, UserService userService) {
        super(environment.getProperty("telegram.botToken"), environment.getProperty("telegram.botUsername"));
        responseHandler = new ResponseHandler(silent, db, userService, this);
        addExtensions(new HelpAbility(this));
    }

    private final ResponseHandler responseHandler;

    public Ability startBot() {
        return Ability.builder()
                .name("start")
                .info("Starts the CarServiceStationBot")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Ability processPhoto() {
        return Ability.builder()
                .name(DEFAULT)
                .flag(PHOTO)
                .locality(USER)
                .privacy(PUBLIC)
                .input(0)
                .action(ctx -> responseHandler.replyToButtons(ctx.chatId(), ctx.update().getMessage()))
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToButtons(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsActive(getChatId(upd)));
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}