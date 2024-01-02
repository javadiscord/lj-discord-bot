package com.javadiscord.bot.commands.slash.impl;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class PingSlashCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }

    @Override
    public String getDescription() {
        return "Ping!";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }
}
