package com.alexkrasnova.githubrepositoryservice.dto;

import java.util.List;

public record RepositoryDTO(String name, String ownerLogin, List<BranchDTO> branches) {
}
