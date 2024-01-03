package com.javadiscord.bot.utils.logging;

import net.dv8tion.jda.api.JDA;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(
        name = "DiscordAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class DiscordAppender extends AbstractAppender {
    private static JDA jda;

    protected DiscordAppender(
            String name, Filter filter, Layout<?> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    public static void setJda(JDA jda) {
        DiscordAppender.jda = jda;
    }

    @Override
    public void append(LogEvent event) {
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {

            jda.getTextChannelsByName("bot-logs", true).stream()
                    .findFirst()
                    .ifPresent(
                            textChannel ->
                                    textChannel
                                            .sendMessage(event.getMessage().getFormattedMessage())
                                            .queue());
        }
    }

    @PluginFactory
    public static DiscordAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DiscordAppender(name, filter, layout, true);
    }
}
