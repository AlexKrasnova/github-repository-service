package com.alexkrasnova.githubrepositoryservice.service;

import com.alexkrasnova.githubrepositoryservice.client.github.GithubClient;
import com.alexkrasnova.githubrepositoryservice.client.github.dto.BranchGithubDTO;
import com.alexkrasnova.githubrepositoryservice.dto.RepositoryDTO;
import com.alexkrasnova.githubrepositoryservice.mapper.RepositoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepositoryService {

    private final GithubClient githubClient;

    private final RepositoryMapper repositoryMapper;

    public List<RepositoryDTO> findByOwner(String ownerLogin) {
        log.info("getting repositories of user " + ownerLogin);
        var repositories = githubClient.findRepositoriesByOwner(ownerLogin);

        return repositories.stream()
                .filter(x -> !x.fork())
                .map(repository -> repositoryMapper.mapToRepositoryDTO(repository, getBranches(ownerLogin, repository.name())))
                .toList();
    }

    private List<BranchGithubDTO> getBranches(String owner, String repositoryName) {
        return githubClient.findBranchesByRepositoryAndUser(owner, repositoryName);
    }
}
