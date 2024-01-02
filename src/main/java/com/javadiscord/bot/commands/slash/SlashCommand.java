package com.javadiscord.bot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public interface SlashCommand {
    void handle(SlashCommandInteractionEvent event);

    String getDescription();

    List<SlashCommandOption> getOptions();
}
