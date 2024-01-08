package com.javadiscord.bot.commands.roleplay;

import com.fasterxml.jackson.databind.JsonNode;
import com.javadiscord.bot.commands.text.TextCommand;
import com.javadiscord.bot.utils.Tenor;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RolePlayCommand implements TextCommand {
    @Override
    public void handle(MessageReceivedEvent event, String input) {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] split = content.split("-");
        String action = split[1].trim();
        List<Member> mentions = message.getMentions().getMembers();
        if (!mentions.isEmpty()) {
            String from = event.getAuthor().getAsMention();
            StringBuilder names = new StringBuilder();
            mentions.forEach(
                    m -> {
                        names.append(m.getAsMention());
                        names.append(" ");
                    });

            if (names.toString().trim().equals("**")) {
                return;
            }

            String searchTerm = action.replaceAll(" ", "%20") + "ing%20anime";
            JsonNode json = Tenor.search(searchTerm, 50);

            if (json != null && json.has("results")) {
                JsonNode results = json.get("results");
                JsonNode result = results.get(ThreadLocalRandom.current().nextInt(results.size()));
                JsonNode media = result.get("media").get(0);
                JsonNode gif = media.get("gif");
                String url = gif.get("url").asText();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription("**" + from + "** " + action + " **" + names + "**");
                embedBuilder.setImage(url);
                embedBuilder.setColor(Color.RED);
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
