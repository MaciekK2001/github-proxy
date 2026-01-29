package com.example.task.Client;

import com.example.task.Model.GithubBranch;
import com.example.task.Model.GithubRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GithubClient {

    private final RestClient restClient;

    GithubClient(RestClient.Builder builder, @Value("${github.api.base-url}") String baseUrl,
                 @Value("${github.token}") String token) {
        if(token.isEmpty()){
            this.restClient = builder
                    .baseUrl(baseUrl)
                    .build();
        }else {
            this.restClient = builder
                    .baseUrl(baseUrl)
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();
        }
    }

    public List<GithubRepository> getRepositories(String username) {
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
    }

    public List<GithubBranch> getBranches(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}