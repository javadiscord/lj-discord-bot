package com.javadiscord.bot;

import com.javadiscord.bot.commands.slash.SlashCommandOption;
import com.javadiscord.bot.commands.slash.SlashCommandRepository;
import com.javadiscord.bot.events.AutoCloseQuestionEvent;
import com.javadiscord.bot.events.BumpReminderEvent;
import com.javadiscord.bot.events.JavaNewsRssEvent;
import com.javadiscord.bot.events.MemeSenderEvent;
import com.javadiscord.bot.events.UpdateMemberCountEvent;
import com.javadiscord.bot.listener.*;
import com.javadiscord.bot.utils.Executor;
import com.javadiscord.bot.utils.logging.DiscordAppender;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public static void main(String[] args) throws InterruptedException {
        JDABuilder jdaBuilder = JDABuilder.createDefault(BOT_TOKEN);
        JDA jda =
                jdaBuilder
                        .setStatus(OnlineStatus.ONLINE)
                        .setEnabledIntents(List.of(GatewayIntent.values()))
                        .build()
                        .awaitReady();
        DiscordAppender.setJda(jda);

        if (jda.getStatus() == JDA.Status.CONNECTED) {
            startEvents(jda);
            registerSlashCommands(jda);
            registerListeners(jda);
        }
    }

    private static void registerListeners(JDA jda) {
        jda.addEventListener(new QuestionListener());
        jda.addEventListener(new RolePlayMessageListener());
        jda.addEventListener(new SlashCommandListener());
        jda.addEventListener(new SpamListener());
        jda.addEventListener(new SuggestionListener());
        jda.addEventListener(new TextCommandListener());
    }

    private static void startEvents(JDA jda) {
        Executor.run(new JavaNewsRssEvent(jda), 12, TimeUnit.HOURS);
        Executor.run(new UpdateMemberCountEvent(jda), 0, 1, TimeUnit.DAYS);
        Executor.run(new BumpReminderEvent(jda), 0, 30, TimeUnit.MINUTES);
        Executor.run(new MemeSenderEvent(jda), 12, TimeUnit.HOURS);
        Executor.run(new AutoCloseQuestionEvent(jda), 1, TimeUnit.HOURS);
    }

    private static void registerSlashCommands(JDA jda) {
        List<SlashCommandData> slashCommands = new ArrayList<>();
        SlashCommandRepository.getCommands()
                .forEach(
                        (k, v) -> {
                            SlashCommandData data = Commands.slash(k, v.getDescription());
                            if (v.getOptions() != null) {
                                for (SlashCommandOption option : v.getOptions()) {
                                    data.addOption(
                                            option.type(), option.name(), option.description());
                                }
                            }
                            slashCommands.add(data);
                        });
        jda.updateCommands().addCommands(slashCommands).queue();
    }
}
