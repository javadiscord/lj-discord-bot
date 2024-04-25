package com.javadiscord.bot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateMemberCountEvent implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateMemberCountEvent.class);
    private static final long CATEGORY_ID = 1152638546725838960L;
    private final JDA jda;

    public UpdateMemberCountEvent(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        Category categories = jda.getCategoryById(CATEGORY_ID);
        if (categories != null) {
            int totalMembers = categories.getGuild().getMemberCount();
            categories.getManager().setName("Members %d".formatted(totalMembers)).queue();
            LOGGER.trace(
                    "Updated {} categoryId title to show {} members", categories, totalMembers);
        } else {
            LOGGER.warn("Could not find category with id: {}", CATEGORY_ID);
        }
    }
}
