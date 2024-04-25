package com.javadiscord.bot.commands.text.impl;

import com.javadiscord.bot.commands.text.TextCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClearChannelCommand implements TextCommand {

    @Override
    public void handle(MessageReceivedEvent event, String input) {
        if (hasRole(event, "Administrator")) {
            int limit;
            try {
                limit = Integer.parseInt(input);
                if (limit <= 1) {
                    limit = 2;
                }
                if (limit > 100) {
                    limit = 100;
                }
            } catch (Exception e) {
                limit = 10;
            }

            List<Message> messages = new ArrayList<>();
            try {
                messages =
                        event.getChannel()
                                .getHistoryBefore(event.getMessageId(), limit)
                                .submit()
                                .get()
                                .getRetrievedHistory();
            } catch (InterruptedException | ExecutionException ignore) {
                /* Ignored */
            }

            event.getGuildChannel().deleteMessages(messages).queue();
        }
    }
}
