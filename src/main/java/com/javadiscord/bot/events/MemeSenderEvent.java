package com.javadiscord.bot.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

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
                downloadImage(memes[0].image).ifPresent(this::sendMessageToChannel);
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            LOGGER.error("Failed to fetch memes", e);
        }
    }

    private void sendMessageToChannel(byte[] image) {
        List<TextChannel> channels = jda.getTextChannelsByName(MEME_CHANNEL, true);
        if (!channels.isEmpty()) {
            TextChannel channel = channels.getFirst();
            channel.sendFiles(FileUpload.fromData(image, "meme.png")).queue();
        } else {
            LOGGER.warn("Could not find {} channel", MEME_CHANNEL);
        }
    }

    private Optional<byte[]> downloadImage(String imageURL) {
        try {
            URL url = new URL(imageURL);
            URLConnection connection = url.openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return Optional.of(byteArrayOutputStream.toByteArray());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to download image", e);
        }
        return Optional.empty();
    }
}
