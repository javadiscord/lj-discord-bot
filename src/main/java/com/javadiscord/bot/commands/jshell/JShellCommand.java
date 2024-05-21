package com.javadiscord.bot.commands.jshell;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.Color;
import java.util.List;

public class JShellCommand implements SlashCommand {
    private static final String CODE_PARAMETER = "code";

    private final JShellService jShellService;

    public JShellCommand(JShellService jShellService) {
        this.jShellService = jShellService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (member == null) {
            event.reply("Member that executed the command is no longer available, won't execute")
                    .queue();
            return;
        }

        long start = System.currentTimeMillis();

        OptionMapping input = event.getOption(CODE_PARAMETER);

        if (input == null) {
            event.reply("Please provide a value for the code parameter").queue();
            return;
        }

        String code = input.getAsString();

        jShellService.updateHistory(event.getMember().getIdLong(), code);

        event.deferReply().queue();

        String memberName = member.getEffectiveName();
        String memberAvatar = member.getAvatarUrl();

        JShellResponse response = jShellService.sendRequest(code);

        if (response == null) {
            String reply = "Failed to execute the provided code, was it bad?";
            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(memberName, null, memberAvatar);
            eb.setDescription(reply);
            eb.setColor(Color.ORANGE);
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        if (response.error() != null && !response.error().isEmpty()) {
            String reply =
                    """
                    An error occurred while executing command:

                    ```java
                    %s
                    ```

                    %s
                    """
                            .formatted(code, response.error());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(memberName, null, memberAvatar);
            eb.setDescription(reply);
            eb.setColor(Color.RED);
            event.getHook().editOriginalEmbeds(eb.build()).queue();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Snippets\n");

        for (JShellSnippet snippet : response.events()) {
            sb.append("`");
            sb.append(snippet.statement());
            sb.append("`\n\n");
            sb.append("**Status**: ");
            sb.append(snippet.status());
            sb.append("\n");

            if (snippet.value() != null && !snippet.value().isEmpty()) {
                sb.append("**Output**\n");
                sb.append("```java\n");
                sb.append(snippet.value());
                sb.append("```\n");
            }
        }

        sb.append("## Console Output\n");
        sb.append("```java\n");
        sb.append(response.outputStream());
        sb.append("```\n");

        if (response.errorStream() != null && !response.errorStream().isEmpty()) {
            sb.append("## Error Output\n");
            sb.append("```java\n");
            sb.append(response.errorStream());
            sb.append("```\n");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(memberName, null, memberAvatar);
        if (sb.length() > 4000) {
            eb.setDescription(sb.substring(0, 4000));
        } else {
            eb.setDescription(sb.toString());
        }
        eb.setColor(Color.GREEN);
        eb.setFooter("Time taken: " + (System.currentTimeMillis() - start) + "ms");

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    @Override
    public String getDescription() {
        return "Run Java code using JShell";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                new SlashCommandOption(
                        OptionType.STRING, CODE_PARAMETER, "The code you would like to execute"));
    }
}
