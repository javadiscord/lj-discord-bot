package com.javadiscord.bot.listener;

import com.javadiscord.bot.utils.chatgpt.ChatGPT;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class QuestionListener extends ListenerAdapter {

    private static final ChatGPT chatGPT = new ChatGPT();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            if (event.isFromThread()) {
                IThreadContainerUnion parent =
                        event.getChannel().asThreadChannel().getParentChannel();
                if (parent.getName().equals("questions")) {
                    ThreadChannel threadChannel = event.getChannel().asThreadChannel();
                    int count = threadChannel.getMessageCount();
                    if (count > 1) {
                        return;
                    }
                    event.getChannel()
                            .sendMessageEmbeds(
                                    TextCommandListener.create(
                                            "",
                                            """
                            # Important

                            Please make sure your question has enough details for a helper to understand the problem.

                            * If you are asking for help with code, please use a code block.
                            * If you are asking for help with an error, please include the full error message.
                            * Screenshots may also be useful. Please do not post screenshots of code, however.

                            """,
                                            "https://media.tenor.com/LoNa2zOMxoAAAAAC/its-very-important-it-matters.gif"))
                            .queue();

                    event.getChannel()
                            .sendMessageEmbeds(
                                    TextCommandListener.create(
                                            "",
                                            """
                            Once your question has been answered, please close this thread by doing `/close`.
                            """,
                                            ""))
                            .queue();

                    List<TextChannel> helpChannels =
                            event.getGuild().getTextChannelsByName("helper-ping", true);
                    if (!helpChannels.isEmpty()) {
                        List<Role> helperRole = event.getGuild().getRolesByName("helper", true);

                        EmbedBuilder helpEmbed = new EmbedBuilder();
                        helpEmbed.setAuthor(
                                threadChannel.getName(), null, event.getAuthor().getAvatarUrl());
                        helpEmbed.setDescription(
                                helperRole.getFirst().getAsMention()
                                        + " \n\n"
                                        + event.getAuthor().getAsMention()
                                        + " has requested some help in "
                                        + parent.getAsMention()
                                        + "\n\n"
                                        + "Please see the following help thread!\n"
                                        + event.getMessage().getJumpUrl()
                                        + "\n\n"
                                        + "Thank you "
                                        + event.getGuild()
                                                .getEmojisByName("HHeartTurtle", true)
                                                .getFirst()
                                                .getAsMention());
                        helpEmbed.setColor(Color.YELLOW);

                        helpChannels.getFirst().sendMessageEmbeds(helpEmbed.build()).queue();

                        StringBuilder answer = new StringBuilder();
                        answer.append("## Here is an attempted answer by ChatGPT\n\n");

                        chatGPT.ask(event.getMessage().getContentRaw())
                                .ifPresentOrElse(
                                        strings -> {
                                            for (String string : strings) {
                                                answer.append(string).append("\n");
                                            }
                                        },
                                        () ->
                                                event.getChannel()
                                                        .sendMessage(
                                                                "ChatGPT is currently unavailable.")
                                                        .queue());

                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setAuthor("", null, "https://chat.openai.com/favicon-32x32.png");
                        eb.setDescription(answer.toString());
                        eb.setColor(Color.CYAN);
                        eb.setFooter(
                                "This is an automated response from ChatGPT. Please do not reply to"
                                    + " this message. You cannot follow up with ChatGPTs message.");

                        event.getChannel().sendMessageEmbeds(eb.build()).queue();
                    }
                }
            }
        }
    }
}
