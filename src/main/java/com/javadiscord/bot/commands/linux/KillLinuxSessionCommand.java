package com.javadiscord.bot.commands.linux;

import com.javadiscord.bot.commands.linux.docker.DockerSessions;
import com.javadiscord.bot.commands.linux.docker.Session;
import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class KillLinuxSessionCommand implements SlashCommand {

    private final DockerSessions dockerSessions;

    public KillLinuxSessionCommand(DockerSessions dockerSessions) {
        this.dockerSessions = dockerSessions;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member != null) {
            Session session = dockerSessions.getSessionForUser(member.getId());
            dockerSessions.removeSession(session);
            event.reply("Your session has been destroyed").queue();
        } else {
            event.reply("Member is no longer available").queue();
        }
    }

    @Override
    public String getDescription() {
        return "Destroy any running Linux sessions you have";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of();
    }
}
