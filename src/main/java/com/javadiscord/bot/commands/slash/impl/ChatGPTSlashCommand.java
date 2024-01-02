package com.javadiscord.bot.commands.slash.impl;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;
import com.javadiscord.bot.utils.chatgpt.ChatGPT;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ChatGPTSlashCommand implements SlashCommand {
    private final ChatGPT chatGPT = new ChatGPT();

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        OptionMapping messageOption = event.getOption("message");
        if (messageOption != null) {
            StringBuilder answer = new StringBuilder();
            answer.append(Objects.requireNonNull(event.getMember()).getAsMention());
            answer.append(" asked:\n");
            answer.append(messageOption.getAsString());
            answer.append("\n");
            answer.append("───────────────\n");
            chatGPT.ask(messageOption.getAsString())
                    .ifPresentOrElse(
                            strings -> {
                                for (String string : strings) {
                                    answer.append(string).append("\n");
                                }
                            },
                            () -> sendChatGptUnavailableMessage(event));
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor("", null, "https://chat.openai.com/favicon-32x32.png");
            eb.setDescription(answer.toString());
            eb.setColor(Color.CYAN);
            eb.setFooter("");
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        } else {
            sendChatGptUnavailableMessage(event);
        }
    }

    private void sendChatGptUnavailableMessage(SlashCommandInteractionEvent event) {
        event.getChannel().sendMessage("ChatGPT is currently unavailable.").queue();
    }

    @Override
    public String getDescription() {
        return "Ask ChatGPT a question";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                new SlashCommandOption(OptionType.STRING, "message", "The prompt to send ChatGPT"));
    }
}
