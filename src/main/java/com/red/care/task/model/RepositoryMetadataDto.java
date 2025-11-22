package com.red.care.task.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RepositoryMetadataDto(
        String id,
        String name,
        @JsonProperty("stargazers_count")
        Integer starsCount,
        String language,
        @JsonProperty("forks_count")
        Integer forksCount,
        @JsonProperty("updated_at")
        Date updatedAt
) {
}
