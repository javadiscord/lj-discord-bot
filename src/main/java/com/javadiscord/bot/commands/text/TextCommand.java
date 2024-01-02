package com.javadiscord.bot.commands.text;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface TextCommand {
    void handle(MessageReceivedEvent event, String input);

    default boolean hasRole(MessageReceivedEvent event, String roleName) {
        boolean hasRole = false;
        if (event.getMember() != null) {
            if (event.getMember().isOwner()) {
                hasRole = true;
            } else {
                for (Role role : event.getMember().getRoles()) {
                    if (role.getName().equalsIgnoreCase(roleName)) {
                        hasRole = true;
                        break;
                    }
                }
            }
        }
        return hasRole;
    }
}
