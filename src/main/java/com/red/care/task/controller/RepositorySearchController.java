package com.red.care.task.controller;

import com.red.care.task.model.SearchRepositoryResponse;
import com.red.care.task.service.RepositorySearchService;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Validated
@RestController("repoSearchApiController")
@RequiredArgsConstructor
@RequestMapping("/v1")
public class RepositorySearchController {

    private final RepositorySearchService repositorySearchService;

    @GetMapping("/repositories-search")
    public ResponseEntity<SearchRepositoryResponse> getRepositories(@RequestParam(value = "createdDate")
                                                                        @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                                        @NonNull
                                                                        final Date createdDate,
                                                                    @RequestParam(value = "language")
                                                                    @NotEmpty
                                                                    final String language) {
        SearchRepositoryResponse response = repositorySearchService.searchRepositories(createdDate, language);
        return ResponseEntity.ok(response);
    }
}
