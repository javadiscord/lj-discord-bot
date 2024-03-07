package com.javadiscord.bot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class AutoCloseQuestionEvent implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCloseQuestionEvent.class);
    private static final String QUESTION_CHANNEL = "questions";
    private static final int AUTO_CLOSE_HOURS = 24;
    private final JDA jda;

    public AutoCloseQuestionEvent(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        List<ForumChannel> forumChannels = jda.getForumChannelsByName(QUESTION_CHANNEL, true);
        if (forumChannels.isEmpty()) {
            LOGGER.warn("Could not find forum channel {}", QUESTION_CHANNEL);
            return;
        }
        ForumChannel forumChannel = forumChannels.getFirst();
        forumChannel
                .getThreadChannels()
                .forEach(
                        threadChannel ->
                                threadChannel
                                        .getHistory()
                                        .retrievePast(1)
                                        .queue(
                                                messages -> {
                                                    if (Duration.between(
                                                                            messages.getFirst()
                                                                                    .getTimeCreated(),
                                                                            OffsetDateTime.now(
                                                                                    ZoneOffset.UTC))
                                                                    .toHours()
                                                            > AUTO_CLOSE_HOURS) {
                                                        threadChannel
                                                                .sendMessage(
                                                                        "This thread has been"
                                                                                + " closed!")
                                                                .queue();
                                                        threadChannel
                                                                .getManager()
                                                                .setArchived(true)
                                                                .queue();
                                                    }
                                                }));
    }
}
