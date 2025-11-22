package com.red.care.task.externalapi.errordecoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class GitHubErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public java.lang.Exception decode(String methodKey, Response response) {
        String message = "GitHub API error";
        try (InputStream body = response.body() != null
                ? response.body().asInputStream()
                : InputStream.nullInputStream()) {
            JsonNode node = objectMapper.readTree(body);
            if (node.has("message")) {
                message = node.get("message").asText();
            }
        } catch (IOException ignored) {
        }

        int status = response.status();
        String fullMessage = "GitHub " + status + ": " + message;

        return switch (status) {
            case 400 -> new GithubException.GitHubClientException(fullMessage);

            case 404 -> new GithubException.GitHubNotFoundException(fullMessage);

            case 429 -> new GithubException.GitHubRateLimitException(fullMessage);

            case 500, 502, 503 -> new GithubException.GitHubServerException(fullMessage);

            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}

