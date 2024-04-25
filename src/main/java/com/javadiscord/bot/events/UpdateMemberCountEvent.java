package com.javadiscord.bot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UpdateMemberCountEvent implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateMemberCountEvent.class);
    private static final String CATEGORY_NAME = "info";
    private final JDA jda;

    public UpdateMemberCountEvent(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        List<Category> categories = jda.getCategoriesByName(CATEGORY_NAME, true);
        if (!categories.isEmpty()) {
            Category infoCategory = categories.getFirst();
            int totalMembers = infoCategory.getGuild().getMemberCount();
            infoCategory.getManager().setName("Members %d".formatted(totalMembers)).queue();
            LOGGER.trace(
                    "Updated {} category title to show {} members", CATEGORY_NAME, totalMembers);
        } else {
            LOGGER.warn("Could not find category with name: {}", CATEGORY_NAME);
        }
    }
}
