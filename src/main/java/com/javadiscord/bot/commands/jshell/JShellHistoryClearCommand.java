package com.javadiscord.bot.commands.jshell;

import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class JShellHistoryClearCommand implements SlashCommand {
    private final JShellService jShellService;

    public JShellHistoryClearCommand(JShellService jShellService) {
        this.jShellService = jShellService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member != null) {
            jShellService.getHistory(event.getMember().getIdLong()).clear();
            event.reply("JShell history has been cleared").queue();
        } else {
            event.reply("Member no longer available").queue();
        }
    }

    @Override
    public String getDescription() {
        return "Clear your JShell History";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of();
    }
}
