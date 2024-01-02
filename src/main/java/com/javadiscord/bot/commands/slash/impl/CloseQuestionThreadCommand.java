package com.javadiscord.bot.commands.slash.impl;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class CloseQuestionThreadCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        try {

            ThreadChannel threadChannel = event.getChannel().asThreadChannel();
            Channel parent = threadChannel.getParentChannel();
            if (parent.getName().equals("questions")) {
                event.reply("This thread has been closed!").queue();
                threadChannel.getManager().setArchived(true).queue();
            }
        } catch (Exception ignored) {
            // ignored
        }
    }

    @Override
    public String getDescription() {
        return "Closes a thread in #questions";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return null;
    }
}
