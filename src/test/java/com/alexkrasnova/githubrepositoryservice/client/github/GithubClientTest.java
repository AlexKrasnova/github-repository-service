package com.alexkrasnova.githubrepositoryservice.client.github;

import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.CommitGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.RepositoryGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.UserGithubDTO;
import com.alexkrasnova.githubrepositoryservice.configuration.GithubRepositoryProperties;
import com.alexkrasnova.githubrepositoryservice.exception.GithubUnavailableException;
import com.alexkrasnova.githubrepositoryservice.exception.UserNotFoundException;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class GithubClientTest {

    private final RestTemplate restTemplate;

    private final GithubClient githubClient;

    public GithubClientTest() {
        RestTemplateBuilder restTemplateBuilder = mock();
        restTemplate = mock();
        GithubRepositoryProperties properties =
                new GithubRepositoryProperties("token", "https://api.github.com");
        doReturn(restTemplateBuilder).when(restTemplateBuilder).rootUri(any());
        doReturn(restTemplateBuilder).when(restTemplateBuilder).additionalInterceptors((ClientHttpRequestInterceptor) any());
        doReturn(restTemplate).when(restTemplateBuilder).build();
        githubClient = new GithubClient(properties, restTemplateBuilder);
    }

    @Test
    public void shouldReturnRepositoriesWhenGithubResponseIs200() {
        // Given
        String username = "alexandra";
        String repositoryName1 = "githubservice";
        String repositoryName2 = "taxiservice";
        RepositoryGithubDTO repository1 = new RepositoryGithubDTO(
                repositoryName1,
                new UserGithubDTO(username),
                false
        );
        RepositoryGithubDTO repository2 = new RepositoryGithubDTO(
                repositoryName2,
                new UserGithubDTO(username),
                false
        );

        doReturn(Arrays.array(repository1, repository2))
                .when(restTemplate)
                .getForObject("/users/{username}/repos", RepositoryGithubDTO[].class, username);

        // When
        List<RepositoryGithubDTO> actual = githubClient.findRepositoriesByOwner(username);

        // Then
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0)).isSameAs(repository1);
        assertThat(actual.get(1)).isSameAs(repository2);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenGithubResponseIs404() {
        // Given
        String username = "alexandra";
        doThrow(new HttpClientErrorException(NOT_FOUND))
                .when(restTemplate)
                .getForObject("/users/{username}/repos", RepositoryGithubDTO[].class, username);

        // When & Then
        assertThatThrownBy(() -> githubClient.findRepositoriesByOwner(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found.");
    }

    @Test
    public void findRepositoriesShouldThrowGithubUnavailableExceptionWhenGithubResponseIs500() {
        // Given
        String username = "alexandra";
        doThrow(new HttpClientErrorException(INTERNAL_SERVER_ERROR))
                .when(restTemplate)
                .getForObject("/users/{username}/repos", RepositoryGithubDTO[].class, username);

        // When & Then
        assertThatThrownBy(() -> githubClient.findRepositoriesByOwner(username))
                .isInstanceOf(GithubUnavailableException.class)
                .hasMessageContaining("Github is unavailable.");
    }

    @Test
    public void shouldReturnBranchesWhenGithubResponseIs200() {
        // Given
        String username = "alexandra";
        String repositoryName = "taxiservice";
        String branchName1 = "main";
        String branchSha1 = "sha1";
        String branchName2 = "develop";
        String branchSha2 = "sha1";
        BranchGithubDTO branch1 = new BranchGithubDTO(
                branchName1,
                new CommitGithubDTO(branchSha1)
        );
        BranchGithubDTO branch2 = new BranchGithubDTO(
                branchName2,
                new CommitGithubDTO(branchSha2)
        );

        doReturn(Arrays.array(branch1, branch2))
                .when(restTemplate)
                .getForObject("/repos/{username}/{repositoryName}/branches", BranchGithubDTO[].class, username, repositoryName);

        // When
        List<BranchGithubDTO> actual = githubClient.findBranchesByRepositoryAndUser(username, repositoryName);

        // Then
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0)).isSameAs(branch1);
        assertThat(actual.get(1)).isSameAs(branch2);
    }

    @Test
    public void findBranchesShouldThrowGithubUnavailableExceptionWhenGithubResponseIs500() {
        // Given
        String username = "alexandra";
        String repositoryName = "taxiservice";
        doThrow(new HttpClientErrorException(INTERNAL_SERVER_ERROR))
                .when(restTemplate)
                .getForObject("/repos/{username}/{repositoryName}/branches", BranchGithubDTO[].class, username, repositoryName);

        // When & Then
        assertThatThrownBy(() -> githubClient.findBranchesByRepositoryAndUser(username, repositoryName))
                .isInstanceOf(GithubUnavailableException.class)
                .hasMessageContaining("Github is unavailable.");
    }
}
