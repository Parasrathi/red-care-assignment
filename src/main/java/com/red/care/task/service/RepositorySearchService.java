package com.red.care.task.service;

import com.red.care.task.externalapi.GithubApiClient;
import com.red.care.task.model.RepositoryMetadata;
import com.red.care.task.model.RepositoryMetadataDto;
import com.red.care.task.model.RepositoryResponse;
import com.red.care.task.model.SearchRepositoryResponse;
import com.red.care.task.util.PopularityScoreCalculator;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.red.care.task.util.QueryBuilder.buildQueryParameter;

@Service
public class RepositorySearchService {

    private final GithubApiClient githubApiClient;
    private final PopularityScoreCalculator popularityScoreCalculator;
    private static final int PER_PAGE_COUNT = 100;
    private static final int PAGE_NUMBER = 1;
    private static final int MAX_PAGES_LIMIT = 10;
    private static final int ZERO = 0;
    private static final int MAX_THREADS = 10;

    public RepositorySearchService(final GithubApiClient githubApiClient,
                                   final PopularityScoreCalculator popularityScoreCalculator) {
        this.githubApiClient = githubApiClient;
        this.popularityScoreCalculator = popularityScoreCalculator;
    }

    public SearchRepositoryResponse searchRepositories(final Date createdDate, final String language) {
        final String query = buildQueryParameter(language, createdDate);
        final RepositoryResponse firstPage = githubApiClient.searchRepositories(query, PER_PAGE_COUNT, PAGE_NUMBER);
        int totalPages = getTotalPages(firstPage.getTotalCount());

        //Github Search API only allows to access first 1000 results.
        if (totalPages > MAX_PAGES_LIMIT) {
            totalPages = MAX_PAGES_LIMIT;
        }

        final List<RepositoryMetadataDto> items = firstPage.getItems();
        final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
        try {
            final List<CompletableFuture<List<RepositoryMetadataDto>>> futures = IntStream.rangeClosed(2, totalPages)
                    .mapToObj(page -> CompletableFuture.supplyAsync(
                            () -> githubApiClient.searchRepositories(query, PER_PAGE_COUNT, page).getItems(), pool)
                    )
                    .toList();

            for (List<RepositoryMetadataDto> pageItems : futures.stream().map(CompletableFuture::join).toList()) {
                items.addAll(pageItems);
            }
        } finally {
            pool.shutdown();
        }
        final List<RepositoryMetadata> repositoryMetadataList = assignPopularityScore(items);
        return SearchRepositoryResponse.builder()
                .repositories(repositoryMetadataList)
                .build();
    }

    private List<RepositoryMetadata> assignPopularityScore(final List<RepositoryMetadataDto> items) {
        return items.stream()
                .map(item -> {
                    double popularityScore = popularityScoreCalculator.calculatePopularityScore(
                            item.starsCount(),
                            item.forksCount(),
                            item.updatedAt()
                    );
                    return buildRepositoryMetadata(item, popularityScore);
                })
                .sorted(Comparator.comparingDouble(RepositoryMetadata::popularityScore)
                                .reversed()
                                .thenComparing(RepositoryMetadata::id) // tie-breaker
                )
                .toList();
    }

    private RepositoryMetadata buildRepositoryMetadata(final RepositoryMetadataDto item, final double popularityScore) {
        return RepositoryMetadata.builder()
                .id(item.id())
                .name(item.name())
                .language(item.language())
                .forksCount(item.forksCount())
                .starsCount(item.starsCount())
                .updatedAt(item.updatedAt())
                .popularityScore(popularityScore)
                .build();
    }

    private int getTotalPages(int totalCount) {
        if (totalCount % PER_PAGE_COUNT == ZERO) {
            return totalCount / PER_PAGE_COUNT;
        } else {
            return (totalCount / PER_PAGE_COUNT) + 1;
        }
    }
}
