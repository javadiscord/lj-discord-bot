package com.javadiscord.bot.commands.slash;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record SlashCommandOption(OptionType type, String name, String description) {}
