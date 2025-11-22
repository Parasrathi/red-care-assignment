package com.red.care.task.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RepositoryResponse {
    @JsonProperty("total_count")
    private int totalCount;
    private List<RepositoryMetadataDto> items;
}
