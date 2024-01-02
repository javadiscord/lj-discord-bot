package com.javadiscord.bot.listener;

import com.javadiscord.bot.commands.roleplay.RolePlayCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

public class RolePlayMessageListener extends ListenerAdapter {
    private static final RolePlayCommand ROLE_PLAY_COMMAND = new RolePlayCommand();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw().trim();
        if (containsRolePlayAction(msg)) {
            ROLE_PLAY_COMMAND.handle(event, msg);
        }
    }

    private static boolean containsRolePlayAction(String message) {
        return message.matches(".*-.*-.*");
    }
}
