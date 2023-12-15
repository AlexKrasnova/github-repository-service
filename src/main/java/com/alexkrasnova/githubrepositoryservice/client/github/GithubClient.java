package com.alexkrasnova.githubrepositoryservice.client.github;

import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.RepositoryGithubDTO;
import com.alexkrasnova.githubrepositoryservice.configuration.GithubRepositoryProperties;
import com.alexkrasnova.githubrepositoryservice.exception.GithubUnavailableException;
import com.alexkrasnova.githubrepositoryservice.exception.UserNotFoundException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class GithubClient {

    private final RestTemplate restTemplate;

    public GithubClient(GithubRepositoryProperties properties, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.githubUrl())
                .additionalInterceptors((request, body, execution) -> {
                    if (properties.token() != null && !properties.token().isBlank()) {
                        request.getHeaders().add("Authorization", "Bearer " + properties.token());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public List<BranchGithubDTO> findBranchesByRepositoryAndUser(String username, String repositoryName) {
        try {
            var branches = restTemplate.getForObject(BRANCHES_URL, BranchGithubDTO[].class, username, repositoryName);
            return Arrays.asList(branches);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new GithubUnavailableException(e);
            }
            throw e;
        }
    }

    public List<RepositoryGithubDTO> findRepositoriesByOwner(String username) {
        try {
            RepositoryGithubDTO[] response = restTemplate.getForObject(REPOSITORIES_URL, RepositoryGithubDTO[].class, username);
            return Arrays.asList(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
                throw new UserNotFoundException(e);
            } else if (e.getStatusCode().is5xxServerError()) {
                throw new GithubUnavailableException(e);
            }
            throw e;
        }
    }

    private static final String BRANCHES_URL = "/repos/{username}/{repositoryName}/branches";

    private static final String REPOSITORIES_URL = "/users/{username}/repos";
}
