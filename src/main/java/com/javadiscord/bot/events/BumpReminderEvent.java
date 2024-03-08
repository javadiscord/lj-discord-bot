package com.javadiscord.bot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class BumpReminderEvent implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateMemberCountEvent.class);
    private static final String BUMP_CHANNEL = "bump";
    private static final String ROLE_TO_NOTIFY = "Bump Notification";
    private static final int BUMP_TIME_HOURS = 2;
    private final JDA jda;

    public BumpReminderEvent(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        List<TextChannel> channels = jda.getTextChannelsByName(BUMP_CHANNEL, true);
        if (!channels.isEmpty()) {
            TextChannel bumpChannel = channels.getFirst();
            bumpChannel
                    .getHistory()
                    .retrievePast(1)
                    .queue(
                            messages -> {
                                if (messages.isEmpty()) {
                                    sendBumpNotification(bumpChannel);
                                } else {

                                    if (!messages.getFirst()
                                                    .getAuthor()
                                                    .getName()
                                                    .equals(jda.getSelfUser().getName())
                                            && Duration.between(
                                                                    messages.getFirst()
                                                                            .getTimeCreated(),
                                                                    OffsetDateTime.now(
                                                                            ZoneOffset.UTC))
                                                            .toHours()
                                                    > BUMP_TIME_HOURS) {
                                        sendBumpNotification(bumpChannel);
                                    }
                                }
                            });
        } else {
            LOGGER.warn("Could not find {} channel", BUMP_CHANNEL);
        }
    }

    private void sendBumpNotification(TextChannel channel) {
        List<Role> roles = jda.getRolesByName(ROLE_TO_NOTIFY, true);
        if (!roles.isEmpty()) {
            LOGGER.info("Sending /bump notification");
            channel.sendMessage(
                            roles.getFirst().getAsMention()
                                    + " please could you bump the server by using Disboards `/bump`"
                                    + " command! Thanks")
                    .queue();
        } else {
            LOGGER.warn("Could not find {} role", ROLE_TO_NOTIFY);
        }
    }
}
