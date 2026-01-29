package com.example.task.Service;

import com.example.task.Client.GithubClient;
import com.example.task.Exception.GithubUserNotFoundException;
import com.example.task.Model.BranchResponse;
import com.example.task.Model.RepositoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class AppService {

    GithubClient githubClient;

    AppService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<RepositoryResponse> getUserRepositories(String username) {
        try {
            return githubClient.getRepositories(username).stream()
                    .filter(repo -> !repo.fork())
                    .map(repo -> {
                        var branches = githubClient.getBranches(
                                        repo.owner().login(),
                                        repo.name()
                                ).stream()
                                .map(branch -> new BranchResponse(
                                        branch.name(),
                                        branch.commit().sha()
                                ))
                                .toList();

                        return new RepositoryResponse(
                                repo.name(),
                                repo.owner().login(),
                                branches
                        );
                    })
                    .toList();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new GithubUserNotFoundException(ex, "User: " + username + " wasn't found.");
        }

    }

}
