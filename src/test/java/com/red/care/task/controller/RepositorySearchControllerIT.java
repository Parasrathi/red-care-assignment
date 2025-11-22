package com.red.care.task.controller;

import com.red.care.task.externalapi.GithubApiClient;
import com.red.care.task.externalapi.errordecoder.GithubException;
import com.red.care.task.model.RepositoryMetadataDto;
import com.red.care.task.model.RepositoryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RepositorySearchControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubApiClient githubApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/v1/repositories-search";
    private static final String FILE_PATH = "src/test/resources/repositorysearchcontroller/expectedresponse/";

    @Test
    void missingBothRequestParams_returnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingCreatedDateRequestParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
//                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingLanguageRequestParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL)
//                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidCreatedDateRequestParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "2025-01-01") //Invalid Format
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void githubApiReturnsEmpty_emptyResponse() throws Exception {
        RepositoryResponse page = RepositoryResponse.builder()
                .totalCount(0)
                .items(new ArrayList<>())
                .build();

        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("language", "javaaa")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readExpectedResponse("empty")));
    }

    @Test
    void happyPath() throws Exception {
        RepositoryResponse page = RepositoryResponse.builder()
                .totalCount(1)
                .items(new ArrayList<>(List.of(mockData("123",10,100))))
                .build();

        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readExpectedResponse("happy_path")));
    }

    @Test
    void happyPath_multiple_repositories() throws Exception {
        when(githubApiClient.searchRepositories(any(), any(), eq(1))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("1",10,100))))
                .build());
        when(githubApiClient.searchRepositories(any(), any(), eq(2))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("2",20,200))))
                .build());
        when(githubApiClient.searchRepositories(any(), any(), eq(3))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("3",30,300))))
                .build());
        when(githubApiClient.searchRepositories(any(), any(), eq(4))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("4",40,400))))
                .build());

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readExpectedResponse("happy_path_2")));
    }

    @Test
    void happyPath_multiple_repositories_same_score() throws Exception {
        when(githubApiClient.searchRepositories(any(), any(), eq(1))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("1",10,100))))
                .build());
        when(githubApiClient.searchRepositories(any(), any(), eq(2))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("2",10,100))))
                .build());
        when(githubApiClient.searchRepositories(any(), any(), eq(3))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("3",10,100))))
                .build());
        when(githubApiClient.searchRepositories(any(), any(), eq(4))).thenReturn(RepositoryResponse.builder()
                .totalCount(400)
                .items(new ArrayList<>(List.of(mockData("4",10,100))))
                .build());

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(readExpectedResponse("happy_path_same_score")));
    }

    @Test
    void githubApi_returns_TooManyRequests() throws Exception {

        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenThrow(new GithubException.GitHubRateLimitException("Github Rate limit exceeded"));

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().json(readExpectedResponse("too_many_requests_error")));
    }

    @Test
    void github400_returns400() throws Exception {
        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenThrow(new GithubException.GitHubClientException("Bad Request"));

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(readExpectedResponse("bad_request_error")));
    }

    @Test
    void github404_returns404() throws Exception {
        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenThrow(new GithubException.GitHubNotFoundException("Not Found"));

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(readExpectedResponse("not_found_error")));
    }

    @Test
    void github500_returns500() throws Exception {
        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenThrow(new GithubException.GitHubServerException("Internal Server Error"));

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdDate", "19-08-2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(readExpectedResponse("internal_server_error")));
    }

    private String readExpectedResponse(final String fileName) throws IOException {
        return readJson(FILE_PATH + fileName + ".json");
    }

    private String readJson(final String fileName) throws IOException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                objectMapper.readValue(new File(fileName), Object.class)
        );
    }

    private RepositoryMetadataDto mockData(String id, int starsCount, int forkCount) throws Exception {
        Date updatedAt = new SimpleDateFormat("dd-MM-yyyy").parse("21-11-2025");
        return new RepositoryMetadataDto(
                id, "demo-repo", starsCount, "Java", forkCount, updatedAt);
    }
}
