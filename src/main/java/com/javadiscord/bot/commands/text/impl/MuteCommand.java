package com.javadiscord.bot.commands.text.impl;

import com.javadiscord.bot.commands.text.TextCommand;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class MuteCommand implements TextCommand {

    @Override
    public void handle(MessageReceivedEvent event, String input) {
        if (hasRole(event, "Administrators")) {
            Role role = event.getJDA().getRolesByName("Muted", true).getFirst();
            List<Member> members = event.getMessage().getMentions().getMembers();
            members.forEach(
                    member -> {
                        event.getGuild().addRoleToMember(member, role).queue();
                        event.getChannel()
                                .sendMessage(member.getAsMention() + " has been muted.")
                                .queue();
                    });
        }
    }
}
