package com.alexkrasnova.githubrepositoryservice.service;

import com.alexkrasnova.githubrepositoryservice.client.github.GithubClient;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.CommitGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.RepositoryGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.UserGithubDTO;
import com.alexkrasnova.githubrepositoryservice.dto.BranchDTO;
import com.alexkrasnova.githubrepositoryservice.dto.RepositoryDTO;
import com.alexkrasnova.githubrepositoryservice.mapper.RepositoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryServiceTest {

    @Mock
    private GithubClient githubClient;

    @Mock
    private RepositoryMapper repositoryMapper;

    @InjectMocks
    private RepositoryService repositoryService;

    @Test
    public void shouldReturnEmptyListWhenTheAreOnlyForks() {
        // Given
        String ownerLogin = "user1";
        UserGithubDTO user = new UserGithubDTO(ownerLogin);
        RepositoryGithubDTO repository1 = new RepositoryGithubDTO(
                "Repository 1",
                user,
                true
        );
        RepositoryGithubDTO repository2 = new RepositoryGithubDTO(
                "Repository 2",
                user,
                true
        );
        List<RepositoryGithubDTO> repositoryGithubDTOList = List.of(repository1, repository2);
        doReturn(repositoryGithubDTOList).when(githubClient).findRepositoriesByOwner(ownerLogin);

        // When
        List<RepositoryDTO> actual = repositoryService.findByOwner(ownerLogin);

        // Then
        assertThat(actual.size()).isEqualTo(0);
        verify(githubClient).findRepositoriesByOwner(ownerLogin);
        verify(githubClient, times(0)).findBranchesByRepositoryAndUser(any(), any());
        verify(repositoryMapper, times(0)).mapToRepositoryDTO(any(), any());
    }

    @Test
    public void shouldReturnListOfRepositories() {
        // Given
        String ownerLogin = "user1";
        UserGithubDTO user1 = new UserGithubDTO(ownerLogin);

        RepositoryGithubDTO repository1 = new RepositoryGithubDTO("Repository 1", user1, false);
        RepositoryGithubDTO repository2 = new RepositoryGithubDTO("Repository 2", user1, false);
        RepositoryGithubDTO repository3 = new RepositoryGithubDTO("Repository 3", user1, true);

        List<RepositoryGithubDTO> repositoryGithubDTOList = List.of(repository1, repository2, repository3);

        BranchGithubDTO branch1 = new BranchGithubDTO("main", new CommitGithubDTO("somesha1"));
        BranchGithubDTO branch2 = new BranchGithubDTO("master", new CommitGithubDTO("somesha2"));
        List<BranchGithubDTO> branches1 = List.of(branch1, branch2);
        List<BranchGithubDTO> branches2 = List.of(branch1);

        doReturn(repositoryGithubDTOList).when(githubClient).findRepositoriesByOwner(ownerLogin);
        doReturn(branches1).when(githubClient).findBranchesByRepositoryAndUser(anyString(), eq("Repository 1"));
        doReturn(branches2).when(githubClient).findBranchesByRepositoryAndUser(anyString(), eq("Repository 2"));

        RepositoryDTO repositoryDTO1 = new RepositoryDTO(
                "Repository 1",
                "user1",
                List.of(new BranchDTO("main", "somesha1"))
        );
        RepositoryDTO repositoryDTO2 = new RepositoryDTO("Repository 2", "user1", List.of());
        doReturn(repositoryDTO1).when(repositoryMapper).mapToRepositoryDTO(repository1, branches1);
        doReturn(repositoryDTO2).when(repositoryMapper).mapToRepositoryDTO(repository2, branches2);

        // When
        List<RepositoryDTO> actual = repositoryService.findByOwner(ownerLogin);

        // Then
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0)).isEqualTo(repositoryDTO1);
        assertThat(actual.get(1)).isEqualTo(repositoryDTO2);

        verify(githubClient).findRepositoriesByOwner(ownerLogin);
        verify(githubClient).findBranchesByRepositoryAndUser(ownerLogin, "Repository 1");
        verify(githubClient).findBranchesByRepositoryAndUser(ownerLogin, "Repository 2");
        verify(repositoryMapper).mapToRepositoryDTO(repository1, branches1);
        verify(repositoryMapper).mapToRepositoryDTO(repository2, branches2);
    }

}
