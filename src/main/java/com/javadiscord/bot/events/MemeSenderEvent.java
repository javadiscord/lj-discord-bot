package com.javadiscord.bot.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MemeSenderEvent implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemeSenderEvent.class);
    private static final String MEME_CHANNEL = "memes";
    private static final String API_URL =
            "https://programming-memes-images.p.rapidapi.com/v1/memes";
    private static final String API_KEY = System.getenv("RAPID_API_KEY");
    private static final String X_RAPID_API_HOST = "programming-memes-images.p.rapidapi.com";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final JDA jda;

    public MemeSenderEvent(JDA jda) {
        this.jda = jda;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MemeResponse(String image) {}

    @Override
    public void run() {
        try {
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(new URI(API_URL))
                            .header("X-RapidAPI-Key", API_KEY)
                            .header("X-RapidAPI-Host", X_RAPID_API_HOST)
                            .GET()
                            .build();

            HttpResponse<String> response =
                    HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.ALWAYS)
                            .build()
                            .send(request, HttpResponse.BodyHandlers.ofString());

            MemeResponse[] memes = OBJECT_MAPPER.readValue(response.body(), MemeResponse[].class);

            if (memes.length > 0) {
                sendMessageToChannel(memes[0].image());
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            LOGGER.error("Failed to fetch memes", e);
        }
    }

    private void sendMessageToChannel(String imageUrl) {
        List<TextChannel> channels = jda.getTextChannelsByName(MEME_CHANNEL, true);
        if (!channels.isEmpty()) {
            TextChannel channel = channels.getFirst();
            channel.sendMessage(imageUrl).queue();
        } else {
            LOGGER.warn("Could not find {} channel", MEME_CHANNEL);
        }
    }
}
