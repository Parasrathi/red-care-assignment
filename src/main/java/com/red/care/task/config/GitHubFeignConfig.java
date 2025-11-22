package com.red.care.task.config;

import com.red.care.task.externalapi.errordecoder.GitHubErrorDecoder;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubFeignConfig {

    private final String githubToken;

    public GitHubFeignConfig(@Value("${github.api.token}") String githubToken) {
        this.githubToken = githubToken;
    }

    @Bean
    public RequestInterceptor githubHeaders() {
        return template -> {
            template.header("Accept", "application/vnd.github+json");
            template.header("X-GitHub-Api-Version", "2022-11-28");
            template.header("Authorization", "Bearer " + this.githubToken);
        };
    }

    @Bean
    public feign.codec.ErrorDecoder errorDecoder() {
        return new GitHubErrorDecoder();
    }
}
