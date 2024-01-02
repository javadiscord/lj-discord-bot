package com.javadiscord.bot.listener;

import com.javadiscord.bot.commands.text.TextCommand;
import com.javadiscord.bot.commands.text.TextCommandRepository;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

public class TextCommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();
            if (message.startsWith("!")) {
                String cmd = message.split(" ")[0].replace("!", "").trim();
                String input = message.replace(String.format("!%s", cmd), "").trim();
                TextCommand command = TextCommandRepository.get(cmd);
                if (command != null) {
                    command.handle(event, input);
                    event.getMessage().delete().queue();
                } else {
                    System.err.println("Command not found.");
                }
            }
        }
    }

    public static MessageEmbed create(String title, String caption, String imageURL) {
        EmbedBuilder eb = new EmbedBuilder();
        if (!imageURL.isEmpty()) {
            eb.setImage(imageURL);
        }
        eb.setAuthor(title, null, null);
        eb.setDescription(caption);
        return eb.build();
    }
}
