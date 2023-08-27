package com.alexkrasnova.githubrepositoryservice.controller;

import com.alexkrasnova.githubrepositoryservice.client.github.GithubClient;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.CommitGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.RepositoryGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.UserGithubDTO;
import com.alexkrasnova.githubrepositoryservice.dto.RepositoryDTO;
import com.alexkrasnova.githubrepositoryservice.dto.error.ErrorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@AutoConfigureMockMvc
@SpringBootTest
public class RepositoryControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private GithubClient githubClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        //It's impossible to use MockBean, because dependency of githubClient is RestTemplateBuilder
        Field restTemplateField
                = GithubClient.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(githubClient, restTemplate);
    }

    @Test
    public void shouldReturnRepositoriesWhenUserExists() throws Exception {
        // Given
        String username = "alexandra";
        String repositoryName1 = "githubservice";
        String repositoryName2 = "taxiservice";
        String branchName1 = "main";
        String branchSha1 = "sha1";
        String branchName2 = "develop";
        String branchSha2 = "sha1";
        String branchName3 = "master";
        String branchSha3 = "sha3";

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

        BranchGithubDTO branch1 = new BranchGithubDTO(
                branchName1,
                new CommitGithubDTO(branchSha1)
        );
        BranchGithubDTO branch2 = new BranchGithubDTO(
                branchName2,
                new CommitGithubDTO(branchSha2)
        );
        BranchGithubDTO branch3 = new BranchGithubDTO(
                branchName3,
                new CommitGithubDTO(branchSha3)
        );

        doReturn(Arrays.array(branch1, branch2))
                .when(restTemplate)
                .getForObject("/repos/{username}/{repositoryName}/branches", BranchGithubDTO[].class, username, repositoryName1);

        doReturn(Arrays.array(branch3))
                .when(restTemplate)
                .getForObject("/repos/{username}/{repositoryName}/branches", BranchGithubDTO[].class, username, repositoryName2);

        // When
        MvcResult result = mvc
                .perform(MockMvcRequestBuilders
                        .get(URL + username)
                        .header("Accept", "application/json")
                ).andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isOk())
                .andReturn();

        // Then
        List<RepositoryDTO> actual = getResponse(result, new TypeReference<>() {
        });
        assertThat(actual.size()).isEqualTo(2);
        RepositoryDTO actualRepositoryOne = actual.get(0);
        assertThat(actualRepositoryOne.name()).isEqualTo(repositoryName1);
        assertThat(actualRepositoryOne.ownerLogin()).isEqualTo(username);
        assertThat(actualRepositoryOne.branches().size()).isEqualTo(2);
        assertThat(actualRepositoryOne.branches().get(0).name()).isEqualTo(branchName1);
        assertThat(actualRepositoryOne.branches().get(0).lastCommitSha()).isEqualTo(branchSha1);
        assertThat(actualRepositoryOne.branches().get(1).name()).isEqualTo(branchName2);
        assertThat(actualRepositoryOne.branches().get(1).lastCommitSha()).isEqualTo(branchSha2);

        RepositoryDTO actualRepositoryTwo = actual.get(1);
        assertThat(actualRepositoryTwo.name()).isEqualTo(repositoryName2);
        assertThat(actualRepositoryTwo.ownerLogin()).isEqualTo(username);
        assertThat(actualRepositoryTwo.branches().size()).isEqualTo(1);
        assertThat(actualRepositoryTwo.branches().get(0).name()).isEqualTo(branchName3);
        assertThat(actualRepositoryTwo.branches().get(0).lastCommitSha()).isEqualTo(branchSha3);
    }

    @Test
    public void shouldReturn404WhenUserDoesNotExist() throws Exception {
        // Given
        String username = "pirog";
        doThrow(new HttpClientErrorException(NOT_FOUND))
                .when(restTemplate)
                .getForObject("/users/{username}/repos", RepositoryGithubDTO[].class, username);

        // When
        MvcResult result = mvc
                .perform(MockMvcRequestBuilders
                        .get(URL + username)
                        .header("Accept", "application/json")
                ).andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isNotFound())
                .andReturn();

        // Then
        ErrorDTO actual = getResponse(result, ErrorDTO.class);
        assertThat(actual.status()).isEqualTo(404);
        assertThat(actual.message()).isEqualTo("User not found.");
    }

    @Test
    public void shouldReturn406WhenGivenXmlAcceptHeader() throws Exception {
        // Given
        String username = "pirog";
        doThrow(new HttpClientErrorException(NOT_FOUND))
                .when(restTemplate)
                .getForObject("/users/{username}/repos", RepositoryGithubDTO[].class, username);

        // When
        MvcResult result = mvc
                .perform(MockMvcRequestBuilders
                        .get(URL + username)
                        .header("Accept", "application/xml")
                ).andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isNotAcceptable())
                .andReturn();

        // Then
        ErrorDTO actual = getResponse(result, ErrorDTO.class);
        assertThat(actual.status()).isEqualTo(406);
        assertThat(actual.message()).isEqualTo("Unsupported 'Accept' header. Must accept 'application/json'.");
    }

    private <T> T getResponse(MvcResult mvcResult, Class<T> valueType) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), valueType);
    }

    private <T> T getResponse(MvcResult mvcResult, TypeReference<T> valueType) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), valueType);
    }

    private static final String URL = "/repositories/by-user/";
}
