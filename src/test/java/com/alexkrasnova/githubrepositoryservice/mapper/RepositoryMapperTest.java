package com.alexkrasnova.githubrepositoryservice.mapper;

import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.CommitGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.RepositoryGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.UserGithubDTO;
import com.alexkrasnova.githubrepositoryservice.dto.RepositoryDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class RepositoryMapperTest {

    private final RepositoryMapper repositoryMapper = new RepositoryMapper();

    @Test
    public void shouldReturnRepositoryDTO() {
        // Given
        String username = "alexandra";
        String repositoryName = "githubservice";
        String branchName1 = "branchName1";
        String branchName2 = "branchName2";
        String sha1 = "Sha1";
        String sha2 = "Sha2";

        RepositoryGithubDTO repositoryGithubDTO = new RepositoryGithubDTO(
                repositoryName,
                new UserGithubDTO(username),
                false
        );

        List<BranchGithubDTO> branchGithubDTOS = List.of(
                new BranchGithubDTO(branchName1, new CommitGithubDTO(sha1)),
                new BranchGithubDTO(branchName2, new CommitGithubDTO(sha2))
        );

        // When
        RepositoryDTO actual = repositoryMapper.mapToRepositoryDTO(repositoryGithubDTO, branchGithubDTOS);

        // Then
        assertThat(actual.name()).isEqualTo(repositoryName);
        assertThat(actual.ownerLogin()).isEqualTo(username);
        assertThat(actual.branches().size()).isEqualTo(2);
        assertThat(actual.branches().get(0).name()).isEqualTo(branchName1);
        assertThat(actual.branches().get(0).lastCommitSha()).isEqualTo(sha1);
        assertThat(actual.branches().get(1).name()).isEqualTo(branchName2);
        assertThat(actual.branches().get(1).lastCommitSha()).isEqualTo(sha2);
    }


}
