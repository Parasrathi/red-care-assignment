package com.red.care.task.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchRepositoryResponse {
    private List<RepositoryMetadata> repositories;
}
