package com.javadiscord.bot.events;

import com.javadiscord.bot.listener.TextCommandListener;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import net.dv8tion.jda.api.JDA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

public class JavaNewsRssEvent implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(JavaNewsRssEvent.class);
    private static final String RSS_URL =
            "https://wiki.openjdk.org/spaces/createrssfeed.action?types=page&spaces=JDKUpdates&maxResults=15&title=%5BJDK+Updates%5D+Pages+Feed&amp;publicFeed=true";
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
                if (isToday(published)) {
                    jda.getTextChannelsByName("java-news", true)
                            .getFirst()
                            .sendMessageEmbeds(
                                    TextCommandListener.create(
                                            entry.getTitle(),
                                            entry.getLink() + "\n" + entry.getPublishedDate(),
                                            ""))
                            .queue();
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred: " + e.getMessage());
        }
    }

    private static boolean isToday(Date date) {
        Calendar todayCalendar = Calendar.getInstance();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        return todayCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)
                && todayCalendar.get(Calendar.DAY_OF_MONTH)
                        == dateCalendar.get(Calendar.DAY_OF_MONTH);
    }
}
