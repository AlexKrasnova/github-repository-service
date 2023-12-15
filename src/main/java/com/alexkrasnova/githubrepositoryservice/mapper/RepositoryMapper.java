package com.alexkrasnova.githubrepositoryservice.mapper;

import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.RepositoryGithubDTO;
import com.alexkrasnova.githubrepositoryservice.dto.BranchDTO;
import com.alexkrasnova.githubrepositoryservice.dto.RepositoryDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RepositoryMapper {

    public RepositoryDTO mapToRepositoryDTO(RepositoryGithubDTO repository, List<BranchGithubDTO> branches) {
        return new RepositoryDTO(
                repository.name(),
                repository.owner().login(),
                branches.stream().map(this::mapToBranchDTO)
                        .toList()
        );
    }

    private BranchDTO mapToBranchDTO(BranchGithubDTO branch) {
        return new BranchDTO(branch.name(), branch.commit().sha());
    }
}
