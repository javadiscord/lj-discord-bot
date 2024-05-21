package com.javadiscord.bot.commands.jshell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JShellResponse(
        @JsonProperty("errorStream") String errorStream,
        @JsonProperty("outputStream") String outputStream,
        @JsonProperty("events") List<JShellSnippet> events,
        @JsonProperty("error") String error) {}
