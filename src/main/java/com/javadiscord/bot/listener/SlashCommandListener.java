package com.javadiscord.bot.listener;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandRepository;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SlashCommandListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandListener.class);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        logger.info(
                Objects.requireNonNull(event.getMember()).getEffectiveName()
                        + " used \"/"
                        + event.getName()
                        + "\" in "
                        + event.getChannel().getName());
        User user = event.getUser();
        if (!user.isBot()) {
            String name = event.getName();
            SlashCommand command = SlashCommandRepository.get(name);
            if (command != null) {
                command.handle(event);
            } else {
                event.reply("Command not found").queue();
            }
        }
    }
}
