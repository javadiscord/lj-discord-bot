package com.javadiscord.bot.commands.slash;

import com.javadiscord.bot.commands.jshell.JShellCommand;
import com.javadiscord.bot.commands.jshell.JShellHistoryClearCommand;
import com.javadiscord.bot.commands.jshell.JShellHistoryCommand;
import com.javadiscord.bot.commands.jshell.JShellService;
import com.javadiscord.bot.commands.slash.impl.CloseQuestionThreadCommand;
import com.javadiscord.bot.commands.slash.impl.PingSlashCommand;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandRepository {
    private static final Map<String, SlashCommand> COMMANDS = new HashMap<>();

    private static final String DOCKER_URL = "tcp://localhost:2375";

    static {
        //  COMMANDS.put("chatgpt", new ChatGPTSlashCommand());
        COMMANDS.put("close", new CloseQuestionThreadCommand());
        COMMANDS.put("ping", new PingSlashCommand());

        JShellService jShellService = new JShellService();
        COMMANDS.put("jshell", new JShellCommand(jShellService));
        COMMANDS.put("jshell-history", new JShellHistoryCommand(jShellService));
        COMMANDS.put("jshell-history-clear", new JShellHistoryClearCommand(jShellService));

        /*
        DockerClient dockerClient = DockerClientBuilder.getInstance(DOCKER_URL).build();
        DockerSessions dockerSessions = new DockerSessions(dockerClient);
        COMMANDS.put(
                "linux",
                new LinuxCommand(
                        dockerClient, dockerSessions, new DockerCommandRunner(dockerClient)));
        COMMANDS.put("linux-close", new KillLinuxSessionCommand(dockerSessions));
        */
    }

    public static SlashCommand get(String key) {
        return COMMANDS.get(key);
    }

    public static Map<String, SlashCommand> getCommands() {
        return COMMANDS;
    }
}
