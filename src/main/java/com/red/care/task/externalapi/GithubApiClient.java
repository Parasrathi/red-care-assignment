package com.red.care.task.externalapi;

import com.red.care.task.config.GitHubFeignConfig;
import com.red.care.task.model.RepositoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "githubClient",
        url = "${github.api.base-url}",
        configuration = GitHubFeignConfig.class
)
public interface GithubApiClient {

    @GetMapping("/search/repositories")
    RepositoryResponse searchRepositories(@RequestParam("q") String query,
            @RequestParam(value = "per_page", required = false) Integer perPage,
            @RequestParam(value = "page", required = false) Integer page
    );
}