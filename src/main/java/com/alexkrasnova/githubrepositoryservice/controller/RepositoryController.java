package com.alexkrasnova.githubrepositoryservice.controller;

import com.alexkrasnova.githubrepositoryservice.dto.RepositoryDTO;
import com.alexkrasnova.githubrepositoryservice.service.RepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.ACCEPT;

@RequestMapping("/repositories")
@RestController
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;

    @Operation(summary = "Get list of repositories by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RepositoryDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "406", description = "Unsupported 'Accept' header.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                    content = @Content),
            @ApiResponse(responseCode = "503", description = "Github is unavailable.",
                    content = @Content)})
    @GetMapping(value = "/by-user/{username}")
    public List<RepositoryDTO> findByUsername(
            @Parameter(description = "Owner login") @PathVariable String username,
            @RequestHeader(ACCEPT) String accept) throws HttpMediaTypeNotAcceptableException {
        checkAcceptHeader(accept);
        return repositoryService.findByOwner(username);
    }

    private void checkAcceptHeader(String accept) throws HttpMediaTypeNotAcceptableException {
        List<String> acceptableAcceptHeaderValues = List.of("application/json", "*/*", "application/*", "*/json");
        if (!acceptableAcceptHeaderValues.contains(accept)) {
            throw new HttpMediaTypeNotAcceptableException("HTTP Media Type Not Acceptable");
        }
    }
}
