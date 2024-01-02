package com.javadiscord.bot.commands.slash;

import com.javadiscord.bot.commands.slash.impl.ChatGPTSlashCommand;
import com.javadiscord.bot.commands.slash.impl.CloseQuestionThreadCommand;
import com.javadiscord.bot.commands.slash.impl.PingSlashCommand;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandRepository {
    private static final Map<String, SlashCommand> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("chatgpt", new ChatGPTSlashCommand());
        COMMANDS.put("close", new CloseQuestionThreadCommand());
        COMMANDS.put("ping", new PingSlashCommand());
    }

    public static SlashCommand get(String key) {
        return COMMANDS.get(key);
    }

    public static Map<String, SlashCommand> getCommands() {
        return COMMANDS;
    }
}
