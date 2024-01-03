package com.javadiscord.bot.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Tenor {
    private static final Logger logger = LoggerFactory.getLogger(Tenor.class);
    private static final String API_KEY = System.getenv("TENOR_API_KEY");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static JsonNode search(String searchTerm, int limit) {
        final String url =
                String.format(
                        "https://api.tenor.com/v1/search?q=%1$s&key=%2$s&limit=%3$s",
                        searchTerm, API_KEY, limit);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .build();

        try {
            HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return OBJECT_MAPPER.readTree(response.body());
            } else {
                System.err.println("HTTP Code: " + response.statusCode() + " from " + url);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error making a request to Tenor", e);
        }

        return null;
    }
}
