package com.javadiscord.bot.listener;

import com.javadiscord.bot.utils.CurseWords;
import com.javadiscord.bot.utils.Executor;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SpamListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(SpamListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getMessage().getAuthor().isBot()
                && CurseWords.containsCurseWord(event.getMessage().getContentRaw())) {

            event.getMessage().delete().queue();
            event.getMessage()
                    .getAuthor()
                    .openPrivateChannel()
                    .queue(
                            channel ->
                                    channel.sendMessage(
                                                    """
                    Your message has been removed for containing words blacklisted by this server!
                    Please avoid sending such messages in the future. Thank you.

                    The message you sent:
                    > %s

                    """
                                                            .formatted(
                                                                    event.getMessage()
                                                                            .getContentRaw()))
                                            .queue());
        }
        muteIfNeeded(event);
    }

    private static class Spam {
        private final String mention;
        private long lastMessage;
        private int count;

        public Spam(String mention, long lastMessage, int count) {
            this.mention = mention;
            this.lastMessage = lastMessage;
            this.count = count;
        }

        public String getMention() {
            return mention;
        }

        public long getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(long lastMessage) {
            this.lastMessage = lastMessage;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    private static final List<Spam> spamList = new ArrayList<>();

    private static Spam get(String mention) {
        Spam spam = null;
        for (Spam s : spamList) {
            if (s.getMention().equalsIgnoreCase(mention)) {
                spam = s;
                break;
            }
        }
        if (spam == null) {
            spam = new Spam(mention, System.currentTimeMillis(), 1);
            spamList.add(spam);
        }
        return spam;
    }

    private static Spam update(String mention) {
        Spam spam = get(mention);
        spam.setCount(spam.getCount() + 1);
        return spam;
    }

    public static void muteIfNeeded(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannel().getName().equalsIgnoreCase("spam")) {
            return;
        }
        Spam spam = update(event.getAuthor().getAsMention());
        var last = spam.getLastMessage();
        var current = System.currentTimeMillis();
        spam.setLastMessage(System.currentTimeMillis());
        if ((current - last) < 1500) {
            if (spam.getCount() >= 3) {
                Role mute = event.getJDA().getRolesByName("Muted", true).getFirst();
                event.getGuild()
                        .addRoleToMember(Objects.requireNonNull(event.getMember()), mute)
                        .queue();
                event.getChannel()
                        .sendMessage(
                                event.getAuthor().getAsMention()
                                        + " has been muted for 1 minute for spamming.")
                        .queue();
                logger.info(
                        event.getAuthor().getAsMention()
                                + " has been muted for 1 minute for spamming.");
                Executor.execute(
                        () -> {
                            try {
                                TimeUnit.MINUTES.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            spam.setCount(0);
                            event.getGuild().removeRoleFromMember(event.getMember(), mute).queue();
                            logger.info(event.getAuthor().getAsMention() + " has been unmuted");
                        });
            }
        } else {
            spam.setCount(0);
        }
    }
}
