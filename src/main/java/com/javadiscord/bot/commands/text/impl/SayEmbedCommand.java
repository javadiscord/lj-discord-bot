package com.javadiscord.bot.commands.text.impl;

import com.javadiscord.bot.commands.text.TextCommand;
import com.javadiscord.bot.listener.TextCommandListener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SayEmbedCommand implements TextCommand {
    @Override
    public void handle(MessageReceivedEvent event, String input) {
        if (hasRole(event, "Administrator")) {
            event.getChannel().sendMessageEmbeds(TextCommandListener.create("", input, "")).queue();
        }
    }
}
