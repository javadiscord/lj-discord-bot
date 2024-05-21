package com.javadiscord.bot.commands.linux;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.javadiscord.bot.commands.linux.docker.*;
import com.javadiscord.bot.commands.slash.SlashCommand;
import com.javadiscord.bot.commands.slash.SlashCommandOption;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class LinuxCommand implements SlashCommand {
    private static final Logger LOGGER = LogManager.getLogger(LinuxCommand.class);

    private static final String COMMAND_PARAMETER = "command";

    private static final ScheduledExecutorService EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor();

    private final DockerClient dockerClient;
    public final DockerSessions dockerSessions;
    private final DockerCommandRunner commandRunner;

    public LinuxCommand(
            DockerClient dockerClient,
            DockerSessions dockerSessions,
            DockerCommandRunner commandRunner) {
        this.dockerClient = dockerClient;
        this.dockerSessions = dockerSessions;
        this.commandRunner = commandRunner;

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () ->
                                        dockerSessions
                                                .getSessions()
                                                .forEach(dockerSessions::stopContainer)));

        EXECUTOR_SERVICE.scheduleAtFixedRate(
                new ContainerCleanupTask(dockerSessions), 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        OptionMapping input = event.getOption(COMMAND_PARAMETER);

        if (input == null) {
            event.reply("Please provide a value for the command parameter").queue();
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply("Member is no longer available").queue();
            return;
        }

        event.deferReply().queue();

        LOGGER.info("Running command {}", input.getAsString());

        Thread.ofVirtual().start(() -> handleLinuxCommand(event, input.getAsString(), member));
    }

    private void handleLinuxCommand(
            SlashCommandInteractionEvent event, String command, Member member) {
        String memberId = member.getId();
        Session session = getSessionForUser(memberId);
        try (OutputStream output = commandRunner.sendCommand(session, command)) {
            String reply =
                    """
                    Ran command:
                    ```
                    $ %s
                    ```

                    ```java
                    %s
                    ```

                    Session expires in %s
                    """
                            .formatted(command, output, getSessionExpiry(session));

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(member.getEffectiveName(), null, member.getAvatarUrl());
            eb.setDescription(shortenOutput(reply));
            eb.setColor(Color.RED);
            event.getHook().editOriginalEmbeds(eb.build()).queue();
        } catch (Exception e) {
            LOGGER.error(e);
            event.getHook().editOriginal("An error occurred: " + e.getMessage()).queue();
        }
    }

    private String getSessionExpiry(Session session) {
        Instant expiry = session.getStartTime().plusSeconds(TimeUnit.MINUTES.toSeconds(5));
        long epochSeconds = expiry.getEpochSecond();
        return "<t:" + epochSeconds + ":R>";
    }

    private String shortenOutput(String input) {
        String concatMessage = "\n**Rest of the output as been removed as it was too long**\n";
        if (input.length() > 4096) {
            input = input.substring(0, 4096 - concatMessage.length()) + concatMessage;
        }
        StringBuilder sb = new StringBuilder();
        String[] parts = input.split("\n");
        if (parts.length > 50) {
            for (int i = 50; i > 0; i--) {
                sb.append(parts[i]).append("\n");
            }
            sb.append(concatMessage);
        } else {
            sb.append(input);
        }
        return sb.toString();
    }

    private Session getSessionForUser(String name) {
        if (!dockerSessions.hasSession(name)) {

            LOGGER.info("Creating new session for {}", name);

            DockerContainerCreator containerCreator = new DockerContainerCreator(dockerClient);

            CreateContainerResponse createContainerResponse =
                    containerCreator.createContainerStarted(
                            "session-" + ThreadLocalRandom.current().nextInt(),
                            "ubuntu:latest",
                            mb(256),
                            mb(256),
                            512,
                            100000,
                            cpuQuota(100000, 0.5));

            return dockerSessions.createSession(name, createContainerResponse.getId());
        }

        LOGGER.info("Found existing session for {}", name);

        return dockerSessions.getSessionForUser(name);
    }

    @Override
    public String getDescription() {
        return "Run commands in your very own Linux session";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                new SlashCommandOption(
                        OptionType.STRING,
                        COMMAND_PARAMETER,
                        "The command you would like to execute"));
    }

    public static long mb(long megabytes) {
        return megabytes * 1024 * 1024;
    }

    public static long cpuQuota(int cpuPeriod, double percentage) {
        return (long) (cpuPeriod * (percentage / 10.0));
    }
}
