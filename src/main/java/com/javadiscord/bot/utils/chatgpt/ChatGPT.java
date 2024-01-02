package com.javadiscord.bot.utils.chatgpt;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChatGPT {
    private static final Logger logger = LoggerFactory.getLogger(ChatGPT.class);
    private static final Duration TIMEOUT = Duration.ofMinutes(3);
    private static final int MAX_TOKENS = 3_000;
    private static final String AI_MODEL = "gpt-3.5-turbo";
    private final OpenAiService openAiService;

    public ChatGPT() {
        openAiService =
                new OpenAiService("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", TIMEOUT);

        ChatMessage setupMessage =
                new ChatMessage(
                        ChatMessageRole.SYSTEM.value(),
                        """
                        Please answer questions in 2000 characters or less. Remember to count spaces in the
                        character limit. The context is Java Programming:\s""");

        ChatCompletionRequest systemSetupRequest =
                ChatCompletionRequest.builder()
                        .model(AI_MODEL)
                        .messages(List.of(setupMessage))
                        .frequencyPenalty(0.5)
                        .temperature(0.3)
                        .maxTokens(50)
                        .n(1)
                        .build();

        openAiService.createChatCompletion(systemSetupRequest);
    }

    public Optional<String[]> ask(String question) {
        try {
            ChatMessage chatMessage =
                    new ChatMessage(ChatMessageRole.USER.value(), Objects.requireNonNull(question));
            ChatCompletionRequest chatCompletionRequest =
                    ChatCompletionRequest.builder()
                            .model(AI_MODEL)
                            .messages(List.of(chatMessage))
                            .frequencyPenalty(0.5)
                            .temperature(0.3)
                            .maxTokens(MAX_TOKENS)
                            .n(1)
                            .build();

            String response =
                    openAiService
                            .createChatCompletion(chatCompletionRequest)
                            .getChoices()
                            .getFirst()
                            .getMessage()
                            .getContent();

            if (response == null) {
                return Optional.empty();
            }

            return Optional.of(AIResponseParser.parse(response));
        } catch (OpenAiHttpException openAiHttpException) {
            logger.warn(
                    String.format(
                            "There was an error using the OpenAI API: %s Code: %s Type: %s Status"
                                    + " Code: %s",
                            openAiHttpException.getMessage(),
                            openAiHttpException.code,
                            openAiHttpException.type,
                            openAiHttpException.statusCode));
        } catch (RuntimeException runtimeException) {
            logger.warn(
                    "There was an error using the OpenAI API: " + runtimeException.getMessage());
        }
        return Optional.empty();
    }
}
