package com.javadiscord.bot.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

public class SuggestionListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getChannel().getName().equals("suggestions")) {
            Message message = event.getMessage();
            Emoji yes = event.getGuild().getEmojisByName("yes", true).getFirst();
            Emoji no = event.getGuild().getEmojisByName("no", true).getFirst();
            message.addReaction(yes).queue();
            message.addReaction(no).queue();
            message.createThreadChannel(message.getContentRaw()).queue();
        }
    }
}
