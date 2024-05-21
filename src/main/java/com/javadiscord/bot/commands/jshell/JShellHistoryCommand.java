package com.javadiscord.bot.commands.jshell;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.List;

public class JShellHistoryCommand implements SlashCommand {
    private static final String HISTORY_PARAMETER = "history";

    private final JShellService jShellService;

    public JShellHistoryCommand(JShellService jShellService) {
        this.jShellService = jShellService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        OptionMapping historyOption = event.getOption(HISTORY_PARAMETER);

        if (historyOption == null) {
            event.reply("Please provide a value for the history parameter").queue();
            return;
        }

        int amount = historyOption.getAsInt();

        List<String> history = jShellService.getHistory(event.getMember().getIdLong());
        StringBuilder sb = new StringBuilder();
        sb.append("## History\n");

        for (String s : history) {
            sb.append("```java\n");
            sb.append(s);
            sb.append("```\n");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getAvatarUrl());
        eb.setDescription(sb.toString());
        eb.setColor(Color.RED);
        event.replyEmbeds(eb.build()).queue();
    }

    @Override
    public String getDescription() {
        return "View your JShell History";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                new SlashCommandOption(
                        OptionType.INTEGER, HISTORY_PARAMETER, "View the past n commands you ran"));
    }
}
