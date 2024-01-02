package com.javadiscord.bot.commands.text.impl;

import com.javadiscord.bot.commands.text.TextCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SayCommand implements TextCommand {
    @Override
    public void handle(MessageReceivedEvent event, String input) {
        if (hasRole(event, "Administrator")) {
            event.getChannel().sendMessage(input).queue();
        }
    }
}
