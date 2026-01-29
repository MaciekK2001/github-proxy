package com.example.task.Controller;

import com.example.task.Exception.GithubUserNotFoundException;
import com.example.task.Model.ErrorResponse;
import com.example.task.Model.RepositoryResponse;
import com.example.task.Service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppController {

    private final AppService appService;

    AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/users/{username}/repositories")
    List<RepositoryResponse> getRepos(@PathVariable String username) {
        return appService.getUserRepositories(username);
    }

    @ExceptionHandler(GithubUserNotFoundException.class)
    ResponseEntity<ErrorResponse> handleUserNotFound(GithubUserNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(new ErrorResponse(404, ex.getMessage()));
    }
}
