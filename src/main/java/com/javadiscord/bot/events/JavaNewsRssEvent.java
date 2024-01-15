package com.javadiscord.bot.events;

import com.javadiscord.bot.listener.TextCommandListener;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class JavaNewsRssEvent implements Runnable {
    private static final Logger logger = LogManager.getLogger(JavaNewsRssEvent.class);
    private static final String RSS_URL =
            "https://wiki.openjdk.org/spaces/createrssfeed.action?types=page&spaces=JDKUpdates&maxResults=15&title=%5BJDK+Updates%5D+Pages+Feed&amp;publicFeed=true";
    private static final String JAVA_NEWS_CHANNEL = "java-news";
    private final JDA jda;

    public JavaNewsRssEvent(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(URI.create(RSS_URL).toURL()));
            for (SyndEntry entry : feed.getEntries()) {
                Date published = entry.getPublishedDate();

                getNewsChannel()
                        .ifPresent(
                                channel -> {
                                    Message message =
                                            channel.getHistory()
                                                    .getMessageById(channel.getLatestMessageId());
                                    if (message != null) {
                                        boolean newMessage =
                                                message.getTimeCreated()
                                                        .toInstant()
                                                        .isBefore(published.toInstant());
                                        if (newMessage) {
                                            channel.sendMessageEmbeds(
                                                            TextCommandListener.create(
                                                                    entry.getTitle(),
                                                                    entry.getLink()
                                                                            + "\n"
                                                                            + entry
                                                                                    .getPublishedDate(),
                                                                    ""))
                                                    .queue();
                                        }
                                    }
                                });
            }
        } catch (Exception e) {
            logger.error("Error occurred: " + e.getMessage());
        }
    }

    private Optional<TextChannel> getNewsChannel() {
        List<TextChannel> channels = jda.getTextChannelsByName(JAVA_NEWS_CHANNEL, true);
        if (channels.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(channels.getFirst());
    }
}
